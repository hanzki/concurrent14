package chat;

import tuplespaces.TupleSpace;

import java.util.*;

import static chat.TupleService.getTuple;
import static chat.TupleService.readTuple;
import static chat.TupleService.putTuple;

public class ChatServer {
    private final TupleSpace tupleSpace;
    private final int rows;

    /**
     * Initializes a new chat network.
     *
     * @param tupleSpace   Tuplespace to use for communication
     * @param rows         Number of rows of backlog to keep for each channel
     * @param channelNames Names of channels in the chat network
     */
    public ChatServer(TupleSpace tupleSpace, int rows, String[] channelNames) {
        this(initTupleSpace(tupleSpace, rows, channelNames));
    }

    /**
     * Connects to an existing chat network.
     *
     * @param tupleSpace Tuplespace to use for communication
     */
    public ChatServer(TupleSpace tupleSpace) {
        this.tupleSpace = tupleSpace;
        SettingsTuple statusTuple = readTuple(tupleSpace, new SettingsTuple());
        rows = statusTuple.getRows();
    }

    public String[] getChannels() {
        SettingsTuple statusTuple = readTuple(tupleSpace, new SettingsTuple());
        return statusTuple.getChannelNames();
    }

    /**
     * Writes a new message to a chat channel.
     *
     * @param channel channel to post message to
     * @param message text of the message
     */
    public void writeMessage(String channel, String message) {
        ChannelTuple channelTuple = getTuple(tupleSpace, new ChannelTuple(channel));
        int newMessageId = channelTuple.getLatestMessageId() + 1;

        //save message to backlog
        putTuple(tupleSpace, new BacklogTuple(channel, newMessageId, message));

        //send message to all the listeners in the channel
        for (UUID listener : channelTuple.getListeners()) {
            putTuple(tupleSpace, new MessageTuple(listener, newMessageId, message));
        }

        //remove oldest message from backlog if necessary
        int oldestMessageId = channelTuple.getOldestMessageId();
        if (newMessageId - rows >= oldestMessageId) {
            getTuple(tupleSpace, new BacklogTuple(channel, oldestMessageId));
            oldestMessageId++;
        }

        ChannelTuple newChannelTuple = new ChannelTuple(channel, oldestMessageId, newMessageId, channelTuple.getListeners());
        putTuple(tupleSpace, newChannelTuple);
    }

    /**
     * Open a new new listener for channel.
     * @param channel Name of the channel to listen to
     * @return A ChatListener which is connected to the chat network
     */
    public ChatListener openConnection(String channel) {
        ChannelTuple channelTuple = getTuple(tupleSpace, new ChannelTuple(channel));
        ChatListener listener = new ChatListener(tupleSpace, channel, channelTuple.getOldestMessageId());
        List<UUID> listeners = channelTuple.getListeners();
        listeners.add(listener.getListenerId());

        //send messages from backlog to the new listener
        for (int msgId = channelTuple.getOldestMessageId(); msgId <= channelTuple.getLatestMessageId(); msgId++) {
            BacklogTuple backlogTuple = readTuple(tupleSpace, new BacklogTuple(channel, msgId));
            putTuple(tupleSpace, new MessageTuple(listener.getListenerId(), msgId, backlogTuple.getMessage()));
        }

        putTuple(tupleSpace, new ChannelTuple(channel, channelTuple.getOldestMessageId(), channelTuple.getLatestMessageId(), listeners));

        return listener;
    }

    private static TupleSpace initTupleSpace(TupleSpace t, int rows, String[] channelNames) {
        SettingsTuple settingsTuple = new SettingsTuple(rows, channelNames);
        putTuple(t, settingsTuple);
        for (String channelName : channelNames) {
            ChannelTuple channelTuple = new ChannelTuple(channelName);
            putTuple(t, channelTuple);
        }
        return t;
    }

}
