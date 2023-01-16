package il.ac.kinneret.mjmay.tom;

import java.net.ServerSocket;

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

        }
    }
}
