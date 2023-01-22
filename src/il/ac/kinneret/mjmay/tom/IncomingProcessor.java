package il.ac.kinneret.mjmay.tom;

import com.sun.corba.se.impl.protocol.SharedCDRClientRequestDispatcherImpl;
import sun.rmi.runtime.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IncomingProcessor extends Thread {

    public IncomingProcessor() {

    }

    public void run() {
        PrintWriter pwOut = null;
        try {
            pwOut = new PrintWriter(new FileOutputStream(SharedState.outputFileName, true));
        } catch (IOException e) {
            Logger.getGlobal().warning("Error opening log file: " + e.getMessage());
            return;
        }
        while (!interrupted()) {
            // open the output file
            // wait until there an incoming message to process and grab it
            Message nextMessage = null;
            try {
                nextMessage = SharedState.incomingMessageQueue.take();
                // now process the message
                if (nextMessage.getType() == Message.MessageType.MESSAGE) {
                    // update our logical clock to the max of what we have and what we received, then increment
                    synchronized (SharedState.queueLocker) {
                        SharedState.localLogicalTimestamp = Math.max(SharedState.localLogicalTimestamp, nextMessage.getLogicalTimeStamp());
                        SharedState.localLogicalTimestamp++;
                    }
                    // put it in the pending queue
                    SharedState.pendingMessages.put(nextMessage);
                    // send ACKs all around
                    String ackMessage = Message.ACK.toString() + "-" + nextMessage.getLogicalTimeStamp() + "-" + nextMessage.getSenderIP();
                    SharedState.outgoingMessageQueue.put(ackMessage);
                } else if (nextMessage.getType() == Message.MessageType.ACK) {
                    // it's an ACK, find the appropriate message in the queue
                    synchronized (SharedState.pendingMessages) {
                        Iterator<Message> it = SharedState.pendingMessages.iterator();
                        boolean done = false;
                        while (it.hasNext() && !done) {
                            Message m = it.next();
                            // see if it has the right logical time and sender
                            if (nextMessage.getSenderIP().equals(m.getSenderIP()) && nextMessage.getLogicalTimeStamp() == m.getLogicalTimeStamp()) {
                                // set the ACK count for it (us and the sender)
                                m.setAcksSoFar(m.getAcksSoFar() + 1);
                                done = true;
                            }
                        }

                        // if we finished and done is false, there wasn't a match
                        if (!done) {
                            Logger.getGlobal().warning("Got an ACK without a message: " + nextMessage);
                            continue;
                        }
                    }
                    // we have an ACK received, see if anything can be removed from the pending queue
                    synchronized (SharedState.pendingMessages) {
                        // go over them and find the one at the head
                        boolean changed = false;
                        Message minMessage = SharedState.pendingMessages.element();
                        do {
                            changed = false;
                            Iterator<Message> it = SharedState.pendingMessages.iterator();
                            while (it.hasNext()) {
                                Message m = it.next();
                                // see if this one is before the min one - either it has a smaller time stamp
                                // or it has the same timestamp, but the senderIP and port are smaller
                                if (m.getLogicalTimeStamp() < minMessage.getLogicalTimeStamp() || (
                                        m.getLogicalTimeStamp() == minMessage.getLogicalTimeStamp() && m.getSenderIP().compareTo(minMessage.getSenderIP()) < 0)) {
                                    // this message is the minimum one for real
                                    minMessage = m;
                                }
                            }

                            // see if the minimum message can be removed (it needs to have an ACK from everyone including
                            // us, so size)
                            if (minMessage.getAcksSoFar() == SharedState.neighbors.size()) {
                                // reinitialize the iterator
                                it = SharedState.pendingMessages.iterator();
                                // find the message we want
                                while (it.hasNext()) {
                                    Message m = it.next();
                                    if (m.getLogicalTimeStamp() == minMessage.getLogicalTimeStamp() &&
                                            m.getSenderIP().equals(minMessage.getSenderIP())) {
                                        it.remove();
                                        changed = true;

                                        // print it to the output file
                                        pwOut.println(minMessage.toString());
                                        pwOut.flush();
                                        break;
                                    }
                                }
                                // we changed the table, maybe something else can come out next time too
                            }
                        } while (changed); // do this until there's no change
                    }
                }
            } catch (InterruptedException e) {
                if ( SharedState.verbose) {
                    Logger.getGlobal().log(Level.INFO, "Something interrupted the incoming message processor: " + e.getMessage());
                }
                break;
            }

        }
        // close the output writer if we need to
        if (pwOut != null)

        {
            pwOut.close();
        }
    }
}
