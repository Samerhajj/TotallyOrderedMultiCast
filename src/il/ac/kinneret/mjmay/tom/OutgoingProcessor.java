package il.ac.kinneret.mjmay.tom;

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
            try {
                String message = SharedState.outgoingMessageQueue.take();

                for (String neighbor:SharedState.neighbors){
                    // TODO: Parse neighbor string to get ip and port,
                    // TODO: Create new socket and outstream message that we got,

                }
            } catch (InterruptedException e) {
                System.err.println("I AM NOT AN ERROR OR A M I?");;
            }
        }

    }


}
