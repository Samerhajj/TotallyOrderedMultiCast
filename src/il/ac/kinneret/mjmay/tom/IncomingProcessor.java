package il.ac.kinneret.mjmay.tom;

import java.io.FileWriter;
import java.io.IOException;

public class IncomingProcessor extends Thread {

    public IncomingProcessor() {

    }

    public void run() {
        // TODO: Fill me in!
        // TODO: Need to listen to incoming messages that are placed on the incoming messages queue. When a message is added to the queue, take it off and process.
        // TODO: Handle the pending message queue here.  When an ACK comes, update the queue and remove messages that can be output now.
    while(!isInterrupted())
    {
        Message incomingMessage;
        synchronized (SharedState.queueLocker) {
            try {
              incomingMessage = SharedState.incomingMessageQueue.take();
            }
            catch (InterruptedException e)
            {

        }
            if(incomingMessage.getType()== Message.MessageType.ACK)
            {
        for(Message pendingMessage:SharedState.pendingMessages){
            if(pendingMessage.getType()==Message.MessageType.MESSAGE &&
                    pendingMessage.getSenderIP() ==incomingMessage.getSenderIP() &&
                    pendingMessage.getLogicalTimeStamp()==incomingMessage.getLogicalTimeStamp()){
                pendingMessage.setAcksSoFar(pendingMessage.getAcksSoFar()+1);
            }


        }
            }
            else if(incomingMessage.getType()==Message.MessageType.MESSAGE){

            }

            SharedState.localLogicalTimestamp=Math.max(SharedState.localLogicalTimestamp,incomingMessage.getLogicalTimeStamp())+1;
            String ackMessage= Message.ACK +"-" +incomingMessage.getLogicalTimeStamp()+"-"+incomingMessage.getSenderIP();

            SharedState.outgoingMessageQueue.put(ackMessage);


            SharedState.pendingMessages.put(incomingMessage);
        } catch (InterruptedException e) {
            System.out.println("ERROR GETTING MESSAGE FROM THE QUEUE  : "+e.getMessage());



    boolean changed=false;
    synchronized (SharedState.queueLocker)
    {
    do{
        Message smallestMessage=null;
        for(Message msg:SharedState.pendingMessages)
        {
            if(smallestMessage==null)
            {
                smallestMessage=msg;
            }
            if(smallestMessage!=null&&
            msg.getLogicalTimeStamp()< smallestMessage.getLogicalTimeStamp() ||
                    (msg.getLogicalTimeStamp()==smallestMessage.getLogicalTimeStamp() &&
                            msg.getSenderIP().compareTo(smallestMessage.getSenderIP())<0)){
                smallestMessage=msg;
            }
        }

        if(smallestMessage !=null &smallestMessage.getAcksSoFar()==SharedState.neighbors.size()){
            SharedState.pendingMessages.remove(smallestMessage);
            try{
                FileWriter fos = new FileWriter(SharedState.outputFileName)){
            fos.write(smallestMessage.toString());
                }
            } catch (IOException ex) {
                System.out.println("Error outputtint to file: " + ex.getMessage());
            }
        }
        else{
            changed=false;
        }
    }while(changed);

    }
        }
}
    }
}
