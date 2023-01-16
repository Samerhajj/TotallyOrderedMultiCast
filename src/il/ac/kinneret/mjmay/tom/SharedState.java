package il.ac.kinneret.mjmay.tom;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class that contains the shared state necessary for the TOM tool to work.  It holds the queues for inter-thread communication
 * and the various shared variables about neighbors and the output file.
 */
public class SharedState {
    /**
     * The queue used to store messages that have arrived at the node
     */
    public static BlockingQueue<Message> incomingMessageQueue;
    /**
     * The queue used to transfer a message to be sent to all neighbors
     */
    public static BlockingQueue<String> outgoingMessageQueue;
    /**
     * The queue that stores all messages that are pending (received technically, but can't be output since there
     * aren't enough ACKs for it yet).
     */
    public static BlockingQueue<Message> pendingMessages;
    /**
     * Used for mutual exclusion on the logical clock and queues.
     */
    public static Object queueLocker;
    /**
     * The neighbors of the node.
     */
    public static Vector<String> neighbors;
    /**
     * Where the output text should be printed
     */
    public static String outputFileName;
    /**
     * The value for the local logical clock
     */
    public static int localLogicalTimestamp;
    /**
     * The name that the node writes on its outgoing messages (IP address and port as a string).
     */
    public static String fromIPPort;
    /**
     * Whether the output logging should be verbose or not
     */
    public static boolean verbose;

    /**
     * Initializes the shared state
     * @param neighbors The list of neighbors to send to
     * @param outputFileName The output file for the messages shown
     */
    public static void initialize(Vector<String> neighbors, String outputFileName, String fromIPPort) {
        incomingMessageQueue = new LinkedBlockingQueue<>();
        outgoingMessageQueue = new LinkedBlockingQueue<>();
        pendingMessages = new LinkedBlockingQueue<>();
        queueLocker = new Object();
        SharedState.neighbors = neighbors;
        SharedState.outputFileName = outputFileName;
        localLogicalTimestamp = 0;
        SharedState.fromIPPort = fromIPPort;
        verbose = false;
    }
}
