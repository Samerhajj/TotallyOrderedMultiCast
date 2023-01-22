package il.ac.kinneret.mjmay.tom;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedState {
    public static BlockingQueue<Message> incomingMessageQueue;
    public static BlockingQueue<String> outgoingMessageQueue;
    public static BlockingQueue<Message> pendingMessages;
    public static Object queueLocker;
    public static Vector<String> neighbors;
    public static String outputFileName;
    public static int localLogicalTimestamp;
    public static String fromIPPort;
    public static boolean verbose;

//    private static SharedState instance = null;

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

//    private static SharedState getInstance() {
//        if (instance == null) {
//            instance = new SharedState();
//        }
//        return instance;
//    }
}

