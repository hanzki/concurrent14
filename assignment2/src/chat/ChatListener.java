package chat;

import tuplespaces.TupleSpace;

import java.util.List;
import java.util.UUID;

import static chat.TupleService.getTuple;
import static chat.TupleService.putTuple;

public class ChatListener {
    private final UUID listenerId;
    private final TupleSpace tupleSpace;
    private final String channel;
    private int nextMsgId;

    private boolean closed = false;

    /**
     * Creates new ChatListner that listens messages from a single channel.
     * @param tupleSpace Chat networks tuplespace
     * @param channel Name of the channel listener listens for
     * @param firstMsgId Id of the first message the listener should receive
     */
    public ChatListener(TupleSpace tupleSpace, String channel, int firstMsgId) {
        this.listenerId = UUID.randomUUID();
        this.tupleSpace = tupleSpace;
        this.channel = channel;
        this.nextMsgId = firstMsgId;
    }

    /**
     * Returns the next message from the channel.
     * @return Text of the message
     * @throws java.lang.IllegalStateException if {@link #closeConnection()} has been called previously
     */
    public String getNextMessage() throws IllegalStateException{
        if(closed) throw new IllegalStateException("Listener is already closed");
        MessageTuple messageTuple = getTuple(tupleSpace, new MessageTuple(listenerId, nextMsgId));
        nextMsgId++;
        return messageTuple.getMessage();
	}

    /**
     * Closes connection to the chat network. Listener's other methods should not be called after calling this.
     * @throws java.lang.IllegalStateException if {@link #closeConnection()} has been called previously
     */
	public void closeConnection() {
        if(closed) throw new IllegalStateException("Listener is already closed");

        ChannelTuple channelTuple = getTuple(tupleSpace, new ChannelTuple(channel));

        //remove listeners messages from tuplespace
        for(;nextMsgId <= channelTuple.getLatestMessageId();){
            getNextMessage();
        }

        //remove listener from tuplespace
        List<UUID> listeners = channelTuple.getListeners();
        listeners.remove(listenerId);
        putTuple(tupleSpace,
                new ChannelTuple(
                        channel,
                        channelTuple.getOldestMessageId(),
                        channelTuple.getLatestMessageId(),
                        listeners
                )
        );
        closed = true;
	}

    public UUID getListenerId() {
        return listenerId;
    }
}
