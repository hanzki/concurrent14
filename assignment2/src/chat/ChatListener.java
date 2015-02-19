package chat;

import tuplespaces.TupleSpace;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import static chat.TupleService.getTuple;
import static chat.TupleService.putTuple;

public class ChatListener {
    private final UUID listenerId;
    private final TupleSpace tupleSpace;
    private final String channel;
    private int nextMsgId;

    private boolean closed = false;

    public ChatListener(TupleSpace tupleSpace, String channel, int nextMsgId) {
        this.listenerId = UUID.randomUUID();
        this.tupleSpace = tupleSpace;
        this.channel = channel;
        this.nextMsgId = nextMsgId;
    }

    public String getNextMessage() {
        if(closed) throw new IllegalStateException("Listener is already closed");
        ChatMessageAlertTuple messageTuple = getTuple(tupleSpace, new ChatMessageAlertTuple(listenerId, nextMsgId));
        nextMsgId++;
        return messageTuple.getMessage();
	}

	public void closeConnection() {
        ChannelStatusTuple channelTuple = getTuple(tupleSpace, new ChannelStatusTuple(channel));
        List<UUID> listeners = channelTuple.getListeners();
        listeners.remove(listenerId);
        putTuple(tupleSpace,
                new ChannelStatusTuple(
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
