package il.ac.kinneret.mjmay.tom;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class OutgoingProcessor extends Thread {

    public OutgoingProcessor() {
    }

    public void run()
    {
        // get the next message or ACK to send
        while (!interrupted())
        {
            // TODO: Fill me in!
            // TODO: When a message is added to the outgoing sending queue, send it to all of the neighbors.

                    while(true)
                    {
                        try {
                            String message = SharedState.outgoingMessageQueue.take();


                            for (String neighbor : SharedState.neighbors) {
                                // TODO: Parse neighbor string to get ip and port,
                                // TODO: Create new socket and outstream message that we got,
                                String part[] = neighbor.split(":");
                                String ip = part[0];
                                int port = Integer.parseInt(part[1]);
                                try( Socket neightborSocket = new Socket(InetAddress.getByName(ip),port)){
                                    PrintWriter pwOut=new PrintWriter(neightborSocket.getOutputStream());
                                    pwOut.println(message);
                                    pwOut.flush();
                                    pwOut.close();
                                }
                                catch (UnknownHostException e)
                                {
                                    continue;
                                }
                                catch (IOException e)
                                {
                                    continue;
                                }
                            }

                        }
                        catch (InterruptedException e)
                        {
                            break;
                        }
                }
            }
        }

    }


