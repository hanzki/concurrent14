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
        SettingsTuple statusTuple = readTuple(t, new SettingsTuple());
        rows = statusTuple.getRows();
    }

	public String[] getChannels() {
		SettingsTuple statusTuple = readTuple(tupleSpace, new SettingsTuple());
		return statusTuple.getChannelNames();
	}

	public void writeMessage(String channel, String message) {
        ChannelTuple channelTuple = getTuple(tupleSpace, new ChannelTuple(channel));
        int newMessageId = channelTuple.getLatestMessageId() + 1;
        putTuple(tupleSpace, new MessageTuple(channel, newMessageId, message));
        for(UUID listener : channelTuple.getListeners()){
            putTuple(tupleSpace, new MessageQueueTuple(listener, newMessageId, message));
        }

        if(newMessageId - rows >= channelTuple.getOldestMessageId()){
            int startIndex = channelTuple.getOldestMessageId();
            int endIndex = newMessageId - rows;
            for(int i = startIndex; i <= endIndex; i++){
                getTuple(tupleSpace, new MessageTuple(channel, i));
            }
        }

        ChannelTuple newChannelTuple =
                new ChannelTuple(
                        channel,
                        Math.max(channelTuple.getOldestMessageId(),
                                newMessageId - rows + 1),
                        newMessageId,
                        channelTuple.getListeners()
                );
        putTuple(tupleSpace, newChannelTuple);
	}

	public ChatListener openConnection(String channel) {
        ChannelTuple channelTuple = getTuple(tupleSpace, new ChannelTuple(channel));
        ChatListener listener = new ChatListener(tupleSpace, channel, channelTuple.getOldestMessageId());
        List<UUID> listeners = channelTuple.getListeners();
        listeners.add(listener.getListenerId());

        for(int msgId = channelTuple.getOldestMessageId(); msgId <= channelTuple.getLatestMessageId(); msgId++){
            MessageTuple messageTuple = readTuple(tupleSpace, new MessageTuple(channel, msgId));
            putTuple(tupleSpace, new MessageQueueTuple(listener.getListenerId(), msgId, messageTuple.getMessage()));
        }

        putTuple(tupleSpace, new ChannelTuple(channel, channelTuple.getOldestMessageId(), channelTuple.getLatestMessageId(), listeners));

        return listener;
	}

    private static TupleSpace initTupleSpace(TupleSpace t, int rows, String[] channelNames){
        SettingsTuple settingsTuple = new SettingsTuple(rows, channelNames);
        putTuple(t, settingsTuple);
        for(String channelName : channelNames){
            ChannelTuple channelTuple = new ChannelTuple(channelName);
            putTuple(t, channelTuple);
        }
        return t;
    }

}
