package il.ac.kinneret.mjmay.tom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Listening extends Thread {

    ServerSocket listeningSocket;

    public Listening(ServerSocket listeningSocket) {
        this.listeningSocket = listeningSocket;
    }

    public void run() {
        // listen on the server socket for incoming conversations
        while (!interrupted() && !listeningSocket.isClosed()) {
            // get the next conversation
            try {
                Socket client = listeningSocket.accept();
                // read the line from the conversation, then close
                BufferedReader brIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String messageIn = brIn.readLine();
                if ( SharedState.verbose) {
                    Logger.getGlobal().info("Got message: " + messageIn);
                }
                // close up
                brIn.close();
                client.close();

                // see if we have a non-null message (may happen if interrupted)
                if (messageIn != null && messageIn.length() > 0) {
                    // put the message in a message object
                    Message newMsg = new Message(messageIn);
                    // add it to the queue
                    SharedState.incomingMessageQueue.put(newMsg);
                }
                // wait for the next one

            } catch (IOException e) {
                if (SharedState.verbose || !e.getMessage().equals("socket closed")) {
                    Logger.getGlobal().log(Level.INFO, "Error reading from conversation: " + e.getMessage());
                }
                break;
            } catch (NullPointerException npe) {
                // something probably canceled us
                Logger.getGlobal().log(Level.INFO, "Null pointer or interrupted exception (server socket closed?): " + npe.getMessage());
                break;
            } catch (InterruptedException e) {
                Logger.getGlobal().log(Level.INFO, "Interrupted when processing incoming message: " + e.getMessage());
                break;
            }
        }
        //
    }
}
