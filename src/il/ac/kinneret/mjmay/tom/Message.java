package il.ac.kinneret.mjmay.tom;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Message {

    public enum MessageType {MESSAGE, ACK, UNKNOWN};

    /**
     * Gets the type of the message
     * @return The type of the message (MESSAGE, ACK, UNKNOWN)
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Gets the logical time stamp for the message
     * @return The logical time stamp for the message (meaningless if the type is UNKNOWN)
     */
    public int getLogicalTimeStamp() {
        return logicalTimeStamp;
    }

    @Override
    public String toString() {
        String out = "";
        switch (type)
        {
            case MESSAGE: out = acksSoFar + " ACKs on MESSAGE-" + logicalTimeStamp + "-" + senderIP + "-" + content; break;
            case ACK: out = "ACK-" + logicalTimeStamp + "-" + senderIP; break;
            default: out = "UNKNOWN"; break;
        }
        return out;
    }

    /**
     * Gets the IP address and port of the message's sender if it's a MESSAGE or the IP and port of the message
     * acknowledged if it's an ACK.
     * @return The IP address and port (10.0.0.2:5000)
     */
    public String getSenderIP() {
        return senderIP;
    }

    /**
     * Gets the content of the message (empty if an ACK or UNKNOWN).
     * @return The content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the total number of ACKs received for the message so far (meaningless if it's an ACK message).
     * @return The number of ACKs so far
     */
    public int getAcksSoFar() {
        return acksSoFar;
    }

    /**
     * Sets the total number of ACKs received for the message so far (meaningless if it's an ACK message).
     */
    public void setAcksSoFar(int acksSoFar) {
        this.acksSoFar = acksSoFar;
    }

    /**
     * The number of ACK for the message so far (meaningless if it's an ACK message itself)
     */
    private int acksSoFar;
    /**
     * The type of the message.
     */
    private MessageType type;
    /**
     * The logical time stamp of the message
     */
    private int logicalTimeStamp;
    /**
     * The IP address and port of the message's sender if it's a MESSAGE or the IP and port of the message
     * acknowledged if it's an ACK.
     */
    private String senderIP;
    /**
     * The content of the message (empty if an ACK or UNKNOWN).
     */
    private String content;

    public static final String MESSAGE = "MESSAGE";
    public static final String ACK = "ACK";

    /**
     * Creates a message object based on a passed string representation.  We assume that there are two types of messages
     * in the system:
     *
     * MESSAGE-1234-10.0.0.2:5000-Text here!
     * ACK-1234-10.0.0.2:5000
     *
     * The first is a message with content.  The second is an ACK of the message
     * @param msg The message received.
     */
    public Message(String msg)
    {
        // see what kind of message we have
        String[] parts = msg.split("-");
        if ( parts.length < 3)
        {
            // this message is bad, just throw it out
            Logger.getGlobal().log(Level.WARNING, "Illegal message received: " + msg);
            type = MessageType.UNKNOWN;
            logicalTimeStamp = 0;
            senderIP = "";
            content = "";
            return;
        }
        else
        {
            // check what type we have
            if ( parts[0].trim().equals(MESSAGE)) {
                // we have a message type, so parse it as such
                if (parts.length < 4) {
                    Logger.getGlobal().log(Level.WARNING, "Illegal message received: " + msg);
                    type = MessageType.MESSAGE;
                    logicalTimeStamp = 0;
                    senderIP = "";
                    content = "";
                    acksSoFar = 0;
                    return;
                }

                type = MessageType.MESSAGE;
                try {
                    logicalTimeStamp = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException nfe) {
                    // bad number
                    Logger.getGlobal().log(Level.WARNING, "Message with bad timestamp received: " + msg);
                    logicalTimeStamp = 0;
                }
                senderIP = parts[2].trim();
                // the content is the rest of the message - find the first - after the colon (the first one will
                // be in the IP address
                content = msg.substring(msg.indexOf("-", msg.indexOf(":"))+1).trim();
                acksSoFar = 0;
            } else if ( parts[0].trim().equals(ACK))
            {
                // we have an ACK, parse it as such
                type = MessageType.ACK;
                try {
                    logicalTimeStamp = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException nfe)
                {
                    Logger.getGlobal().log(Level.WARNING, "ACK with bad timestamp received: " + msg);
                    logicalTimeStamp = 0;
                }
                senderIP = parts[2].trim();
            }
            else
            {
                // something else
                Logger.getGlobal().log(Level.WARNING, "Got a message with an unknown type: " + msg);
                type = MessageType.UNKNOWN;
                logicalTimeStamp = 0;
                senderIP = "";
                content = "";
                acksSoFar = 0;
            }
        }
    }

}
