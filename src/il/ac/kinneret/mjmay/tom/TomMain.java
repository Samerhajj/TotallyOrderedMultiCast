package il.ac.kinneret.mjmay.tom;

import il.ac.kinneret.mjmay.common.Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.Vector;

public class TomMain {

    public static void main (String[] args)
    {
        // make sure we get a port and neighbors list
        if (args.length < 3 )
        {
            showUsage();
            return;
        }

        // get the neighbors list
        Vector<String> neighbors = Common.parseNeighbors(args[0]);

        if (neighbors.size()==0)
        {
            // something is wrong
            showUsage();
            return;
        }

        // get the output file name
        String outputFileName = args[1];

        int port = 0;
        try
        {
            port =Integer.parseInt(args[2]);
        }
        catch (NumberFormatException nfe)
        {
            showUsage();
            return;
        }
        // show the initial startup material (choose IP and all that)
        InetAddress listenAddress = Common.selectIPAddress();

        BufferedReader brKeyboard = new BufferedReader(new InputStreamReader((System.in)));

        String fromName = listenAddress.toString();
        // if the user selected the 0.0.0.0, ask for an IP to write in the from
        if ( listenAddress.isAnyLocalAddress())
        {
            System.out.println("Enter an IP address for the `from' field:");
            try {
                fromName = brKeyboard.readLine();
                // add a leading / for consistency
                if ( !fromName.startsWith("/"))
                {
                    fromName = "/" + fromName;
                }
            } catch (IOException e)
            {
                // something is amiss!
                System.out.println("Error reading from value");
                return;
            }
        }
        // add the port in the end
        fromName = fromName + ":" + port;

        // now start to listen for incoming messages
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 50, listenAddress);
        } catch (IOException e) {
            System.out.println("Error listening on selected IP and port: " + listenAddress.toString() + ":" + port +
                    ": " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // we have a socket to listen on, so start listening
        Listening listening = new Listening(serverSocket);
        listening.start();

        System.out.println("Started to listen on " + serverSocket.toString());
        System.out.println("Sending messages from: " + fromName);

        // start the incoming messages processor
        IncomingProcessor incomingProcessor = new IncomingProcessor();
        incomingProcessor.start();
        System.out.println("Started incoming messages processor.");

        // start the outgoing messages processor
        OutgoingProcessor outgoingProcessor = new OutgoingProcessor();
        outgoingProcessor.start();
        System.out.println("Started outgoing messages processor.");

        boolean quit = false;
        while (!quit) {
            int choice = 0;
            while (choice < 1 || choice > 3) {
                // let the user choose what to do
                System.out.println("Choose what to do:");
                System.out.println("1. Send a new message:");
                System.out.println("2. Print status");
                System.out.println("3. Quit");
                System.out.print(": ");

                try {
                    String lineIn = brKeyboard.readLine();
                    choice = Integer.parseInt(lineIn);
                } catch (IOException | NumberFormatException e) {
                    System.out.println("Error. Try again.");
                    choice = 0;
                }
            }
            // what did the user choose to do?
            switch (choice) {
                case 1:
                    doSendMessage();
                    break;
                case 2:
                    doPrintStatus();
                    break;
                case 3:
                    quit = true;
                    break;
                default:
                    // just do it again
                    quit = false;
                    break;
            }
        }

        // now we quit!
        try {
            listening.interrupt();
            serverSocket.close();
            System.out.println("Stopped listening.");
        } catch (IOException e) {
            System.out.println("Error closing listener: " + e.getMessage());
        }
        incomingProcessor.interrupt();
        System.out.println("Stopped incoming messages processor.");
        outgoingProcessor.interrupt();
        System.out.println("Stopped outgoing messages processor.");

        // show the end status
        System.out.println("Final Status:");
        doPrintStatus();
    }

    /**
     * Prints the status of the pending message queue and shows the current logical time stamp
     */
    private static void doPrintStatus() {
        // print the status of things
        synchronized (SharedState.queueLocker)
        {
            // print the logical time and the whole queue
            System.out.println("Logical clock time: "  + SharedState.localLogicalTimestamp);
            System.out.println("Pending messages:");
            Iterator<Message> it = SharedState.pendingMessages.iterator();
            while (it.hasNext())
            {
                System.out.println(it.next().toString());
            }
        }
    }

    private static void doSendMessage() {
        // TODO: Write the logic to send a new message from the node to others (use the queues).
    }

    private static void showUsage() {
        System.out.println("Usage: TomMain neighborsFile outputFile port");
    }

}
