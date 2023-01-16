package il.ac.kinneret.mjmay.tom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Listens on a server socket for incoming messages.
 */
public class Listening extends Thread {

    /**
     * The socket that we listen on
     */
    ServerSocket listeningSocket;

    public Listening(ServerSocket listeningSocket) {
        this.listeningSocket = listeningSocket;
    }

    public void run() {
        // listen on the server socket for incoming conversations
        while (!interrupted() && !listeningSocket.isClosed()) {
            // TODO: Fill me in!
            // TODO: Listen for incoming messages.  Put them in the correct queue based on their type.
            String line;
            try {
                Socket clientSocket = listeningSocket.accept();
                BufferedReader brIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
               line=brIn.readLine();

            } catch (IOException e)
            {
                System.out.println("ERROR LIstening for incoming messages : " + e.getMessage());
                continue;
            }
            Message message = new Message(line);
            synchronized ()
        }
    }

}