package il.ac.kinneret.mjmay.tom;

import org.omg.PortableInterceptor.INACTIVE;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutgoingProcessor extends Thread {

    public OutgoingProcessor() {
    }

    public void run()
    {
        // get the next message or ACK to send
        while (!interrupted())
        {
            try {
                String nextMessage = SharedState.outgoingMessageQueue.take();
                // build outgoing connections to everyone for this
                for (String neighbor : SharedState.neighbors)
                {
                    // get the address and port
                    String[] parts = neighbor.split(":");
                    try {
                        InetAddress add = InetAddress.getByName(parts[0]);
                        int port = Integer.parseInt(parts[1]);

                        // make an outgoing connection to it
                        Socket socket = new Socket(add, port);
                        PrintWriter pwOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        // send the message
                        pwOut.println(nextMessage);
                        pwOut.flush();
                        // close up shop
                        pwOut.close();
                        socket.close();
                    } catch (IOException e)
                    {
                        // problem connecting or sending
                        Logger.getGlobal().log(Level.WARNING, "Error connecting or sending to neighbor (" + neighbor + "): " + e.getMessage());
                        continue;
                    }
                }
            } catch (InterruptedException e) {
                if (SharedState.verbose) {
                    // got interrupted getting the message, just quit
                    Logger.getGlobal().log(Level.INFO, "Something interrupted the outgoing message processor: " + e.getMessage());
                }
                break;
            }
        }

    }


}
