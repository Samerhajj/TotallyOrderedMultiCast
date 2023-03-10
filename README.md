

##  System Description
The totally ordered multicast system is based upon the following rules:
1. Each process in the system maintains a single logical clock which represents its logical time.
2. When a process wants to send a message to the other processes in the system, it attaches its current
   logical time and process identifier to the message and forwards it to all of the other processes in the
   system. The message is then put in a “pending” queue until all of the other processes in the system
   have acknowledged receiving the message. The queue is sorted by logical clock time. In case of a tie,
   the message with the smaller process identifier goes first.
3. When a process receives a message from another process, it puts the new message in its queue (ordered
   by logical clock and process identifier) and sends an acknowledgement to all of the other processes in
   the system.
4. When a process receives an acknowledgement from another process, it puts the acknowledgement in
   the queue as well.
5. When a message has been acknowledged by all other processes in the system and is at the head of the
   queue, it can be removed from the queue and output. In our tool, we will output the messages that
   have been completely acknowledged to an output text file.

# 1.1 A note on ACKs
Processes don’t have to keep track of who has sent an acknowledgement for a given message; a counter for
each message suffices. Once the expected number of acknowledgements has been received and the message
is at the head of the queue, it can be executed.
To make the tool simpler, we will ensure that all nodes receive n acknowledgements for each message received.
We’ll do that by having each sender record an ACK to itself when it sends a message. That ensures that all
nodes will receive n acknowledgements.
If we didn’t do that, we’d need to enforce a difference between the sender of a message and all other nodes.
Consider a system with n processes P. Let m1 be a message sent by process pi
.
1. pi will receive n − 1 acknowledgements - all processes except for itself.
2. All other processes (pj ∈ P such that i ̸= j) will receive n − 2 acknowledgements. The original sender
   pi will not send an acknowledgement and pj won’t send an acknowledgement to itself.
   As noted above, we will not be making this distinction in our tool - we’ll ensure that all nodes get n ACK

### 1.1.1 Living without ACKs
 it’s possible to build the TOM tool without any ACKs at all. If all nodes submit messages
regularly and we can enforce FIFO and reliable messaging (using ACKs or TCP), we don’t need to use
the ACK mechanism at all. It’s sufficient to see a message from another node to know that it received all
previous ones already.
If we implemented the system without ACKs, we would need to then compare who we have received messages
from so far, not just counting messages as we did with ACKs.

## 1.2 Pending Messages and Output
When a message arrives, it is first put in the pending queue until it accrues enough ACKs to be moved to
the output file. The tool therefore needs to check each incoming message in the following manner:
1. If the message is a MESSAGE (see format below), enter it into the pending message queue immediately.
Write down its important fields. Send an ACK for it to all other nodes (including yourself, whether
by sending a message or just by adding 1 to the ACK counter).

2. If the message is an ACK (see format below), find the corresponding message in the pending queue
and increment the ACK counter for it.

When an ACK arrives, it’s possible that one or more messages can be removed from the pending messages
queue and output to the output file (one of the parameters above). Therefore, after the arrival of an ACK,
you need to go over the pending queue and see which (if any) messages can be deleted from the queue and
output to the output file. Keep in mind that only the head of the pending queue can be removed if it’s
ready. Any message behind the head must wait, no matter how many ACKs it has. If the head is stuck
for some reason (ACKs are late) and a few other older messages are fully acknowledged, once the head is
(finally) released, all of the ready messages behind it will be released as well.


# Protocol Details
The tools communicate among a group of statically defined neighbors. There are two messages sent between
the neighbors:
MESSAGE-1234-10.0.0.2:5000-Text here The message uses - as a field delimiter. It has the following
fields:
1. The logical clock of the sender at the time the message was sent
2. The IP address and port of the sender
3. The text of the message sent.
 ###  ACK-1234-10.0.0.2:5000 The message uses - as a field delimiter. It has the following fields:
1. The logical clock of the message that is being acknowledged.
2. The IP address and port of the sender of the message being acknowledged.


# Threading and Processing
Since we are not writing a GUI, we will need to separate out various pieces of logic into different threads
and use a producer/consumer model. We can identify four threads which need to run in parallel: (a) TheUI thread, (b) the message listening thread, (c) the message sending (outgoing) thread, and (d) the message
processing (incoming) thread.
All threads communicate using a BlockingQueue interface. I used LinkedBlockingQueue as the concrete
class.

![img.png](img.png)

# 4.1 UI Thread
The UI thread offers a menu based interface that enables:
* Send a new message
* Print out the status of the pending queue and the current logical clock (see Figure 3). To print out
the pending queue, the UI thread also needs read access to the pending queue.
* Quit - close all of the threads and the listener
# 4.2 Message Listening Thread
The message listening thread listens on a ServerSocket to get incoming messages. Once an incoming
message arrives, it adds it to the incoming messages queue and goes back to listen.

# 4.3 Message Sending (Outgoing) Thread
When the tool needs to send a message or an ACK, the message is added to the outgoing messages queue.
The outgoing thread consumes from the outgoing messages queue and sends the message to all neighbors in
the neighbors list.
# 4.4 Message Processing (Incoming) Thread
When a message arrives, the incoming messages thread takes it off of the incoming messages queue and
processes it.
* If the message is an ACK, it updates the pending message queue as mentioned above in Section 1.2.
* If the message is a MESSAGE, it processes it as mentioned in Section 1.2 and prepare an ACK to send.
It then puts the ACK in the outgoing message queue to send to all neighbors (including the current
node itself).

As shown above, the ACK message doesn’t contain the body of the message or the IP address of the
acknowledging node, just the logical time stamp, IP address, and port of the original sender of the
message.
