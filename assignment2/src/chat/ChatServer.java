package chat;

import tuplespaces.TupleSpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatServer {
	private final TupleSpace tupleSpace;
    private final Map<String, List<ChatListener>> listeners;
    private final int rows;
    private final String[] channelNames;

	public ChatServer(TupleSpace t, int rows, String[] channelNames) {
		this(initTupleSpace(t,rows,channelNames));
	}

	public ChatServer(TupleSpace t) {
		tupleSpace = t;
        ServerStatusTuple statusTuple = readTuple(new ServerStatusTuple());
        rows = statusTuple.getRows();
        channelNames = statusTuple.getChannelNames();
        listeners = new HashMap<String, List<ChatListener>>();
        for(String channel : channelNames){
            listeners.put(channel, new ArrayList<ChatListener>());
        }
    }

	public String[] getChannels() {
		ServerStatusTuple statusTuple = readTuple(new ServerStatusTuple());
		return statusTuple.getChannelNames();
	}

	public void writeMessage(String channel, String message) {
        if(!listeners.containsKey(channel)) throw new IllegalArgumentException();
        ChannelStatusTuple channelTuple = getTuple(new ChannelStatusTuple(channel));
        ChatMessageTuple messageTuple = new ChatMessageTuple(channel,channelTuple.getFirstMessageId() + 1, message);
        tupleSpace.put(messageTuple.getAsData());

        synchronized (listeners.get(channel)){
            for(ChatListener listener : listeners.get(channel)){
                listener.addMessage(message);
            }
        }

        if(channelTuple.getFirstMessageId() + 1 - rows >= channelTuple.getLastMessageId()){
            int startIndex = channelTuple.getLastMessageId();
            int endIndex = channelTuple.getFirstMessageId() + 1 - rows;
            for(int i = startIndex; i <= endIndex; i++){
                getTuple(new ChatMessageTuple(channel, i));
            }
        }

        ChannelStatusTuple newChannelTuple =
                new ChannelStatusTuple(
                        channel,
                        channelTuple.getFirstMessageId() + 1,
                        Math.max(channelTuple.getLastMessageId(),
                                channelTuple.getFirstMessageId() + 2 - rows)
                );
        tupleSpace.put(newChannelTuple.getAsData());
	}

	public ChatListener openConnection(String channel) {
        if(!listeners.containsKey(channel)) throw new IllegalArgumentException();
		ChatListener listener = new ChatListener(this, channel);

        ChannelStatusTuple ct = getTuple(new ChannelStatusTuple(channel));
        int lastMessageToRetrieve = Math.max(ct.getLastMessageId(), ct.getFirstMessageId() - rows + 1);
        for(int i = lastMessageToRetrieve; i <= ct.getFirstMessageId(); i++){
            ChatMessageTuple mt = readTuple(new ChatMessageTuple(channel, i));
            listener.addMessage(mt.getMessage());
        }
        synchronized (listeners.get(channel)){
            listeners.get(channel).add(listener);
        }
        tupleSpace.put(ct.getAsData());
        return listener;
	}

    public void closeConnection(String channel, ChatListener listener){
        synchronized (listeners.get(channel)){
            listeners.get(channel).remove(listener);
        }
    }

	private <T extends ChatServerTuple<T>> T getTuple(T templateTuple){
		String[] tupleData = tupleSpace.get(templateTuple.getAsTemplate());
		return templateTuple.parseTupleData(tupleData);
	}

	private <T extends ChatServerTuple<T>> T readTuple(T templateTuple){
		String[] tupleData = tupleSpace.read(templateTuple.getAsTemplate());
		return templateTuple.parseTupleData(tupleData);
	}

    private static TupleSpace initTupleSpace(TupleSpace t, int rows, String[] channelNames){
        ServerStatusTuple serverStatusTuple = new ServerStatusTuple(rows, channelNames, 0);
        t.put(serverStatusTuple.getAsData());
        for(String channelName : serverStatusTuple.getChannelNames()){
            ChannelStatusTuple channelStatusTuple = new ChannelStatusTuple(channelName);
            t.put(channelStatusTuple.getAsData());
        }
        return t;
    }

}
