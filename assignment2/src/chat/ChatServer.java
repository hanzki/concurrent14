package chat;

import tuplespaces.TupleSpace;

import java.util.*;

import static chat.TupleService.getTuple;
import static chat.TupleService.readTuple;
import static chat.TupleService.putTuple;

public class ChatServer {
	private final TupleSpace tupleSpace;
    private final int rows;

	public ChatServer(TupleSpace t, int rows, String[] channelNames) {
		this(initTupleSpace(t,rows,channelNames));
	}

	public ChatServer(TupleSpace t) {
		tupleSpace = t;
        ServerStatusTuple statusTuple = readTuple(t, new ServerStatusTuple());
        rows = statusTuple.getRows();
    }

	public String[] getChannels() {
		ServerStatusTuple statusTuple = readTuple(tupleSpace, new ServerStatusTuple());
		return statusTuple.getChannelNames();
	}

	public void writeMessage(String channel, String message) {
        ChannelStatusTuple channelTuple = getTuple(tupleSpace, new ChannelStatusTuple(channel));
        int newMessageId = channelTuple.getLatestMessageId() + 1;
        putTuple(tupleSpace, new ChatMessageTuple(channel, newMessageId, message));
        for(UUID listener : channelTuple.getListeners()){
            putTuple(tupleSpace, new ChatMessageAlertTuple(listener, newMessageId, message));
        }

        if(newMessageId - rows >= channelTuple.getOldestMessageId()){
            int startIndex = channelTuple.getOldestMessageId();
            int endIndex = newMessageId - rows;
            for(int i = startIndex; i <= endIndex; i++){
                getTuple(tupleSpace, new ChatMessageTuple(channel, i));
            }
        }

        ChannelStatusTuple newChannelTuple =
                new ChannelStatusTuple(
                        channel,
                        Math.max(channelTuple.getOldestMessageId(),
                                newMessageId - rows + 1),
                        newMessageId,
                        channelTuple.getListeners()
                );
        putTuple(tupleSpace, newChannelTuple);
	}

	public ChatListener openConnection(String channel) {
        ChannelStatusTuple channelTuple = getTuple(tupleSpace, new ChannelStatusTuple(channel));
        ChatListener listener = new ChatListener(tupleSpace, channel, channelTuple.getOldestMessageId());
        List<UUID> listeners = channelTuple.getListeners();
        listeners.add(listener.getListenerId());

        for(int msgId = channelTuple.getOldestMessageId(); msgId <= channelTuple.getLatestMessageId(); msgId++){
            ChatMessageTuple messageTuple = readTuple(tupleSpace, new ChatMessageTuple(channel, msgId));
            putTuple(tupleSpace, new ChatMessageAlertTuple(listener.getListenerId(), msgId, messageTuple.getMessage()));
        }

        putTuple(tupleSpace, new ChannelStatusTuple(channel, channelTuple.getOldestMessageId(), channelTuple.getLatestMessageId(), listeners));

        return listener;
	}

    private static TupleSpace initTupleSpace(TupleSpace t, int rows, String[] channelNames){
        ServerStatusTuple serverStatusTuple = new ServerStatusTuple(rows, channelNames);
        putTuple(t, serverStatusTuple);
        for(String channelName : channelNames){
            ChannelStatusTuple channelStatusTuple = new ChannelStatusTuple(channelName);
            putTuple(t, channelStatusTuple);
        }
        return t;
    }

}
