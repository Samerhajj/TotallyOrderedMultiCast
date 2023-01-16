package il.ac.kinneret.mjmay.tom;

public class IncomingProcessor extends Thread {

    public IncomingProcessor() {

    }

    public void run() {
        // TODO: Fill me in!
        // TODO: Need to listen to incoming messages that are placed on the incoming messages queue. When a message is added to the queue, take it off and process.
        // TODO: Handle the pending message queue here.  When an ACK comes, update the queue and remove messages that can be output now.
    while(!isInterrupted())
    {
        try{
            Message incomingMessage= SharedState.incomingMessageQueue.take();
            if(incomingMessage.getType()== Message.MessageType.ACK)
            {

            }
            else if(incomingMessage.getType()==Message.MessageType.MESSAGE){

            }
            // If the message is of type MESSAGE, you can add the message to the    pendingMessages
            // queue and prepare an ACK message to send to all neighbors. You can add the ACK message
            // to the outgoingMessageQueue to be sent by the OutgoingProcessor.
            //If the message is of type ACK, you can iterate through the pendingMessages
            // queue and find the corresponding message (using the senderIP, logicalTimeStamp and
            // type fields) and increment the acksSoFar field of that message. Then you can check if
            // the number of acksSoFar equals to the number of neighbors, if yes, you can remove the message
            // from the pendingMessages queue and write it to the output file.


        }
        catch (InterruptedException e)
        {
            System.err.println("OH NO I WAS INTERRUPTED");
        }
    }
    }
}
