package chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hanzki on 21.12.2014.
 */
class ChannelTuple implements ChatServerTuple<ChannelTuple> {
    private static final String SEPARATOR = ":";
    private static final String TUPLE_PREFIX = "CHAN";
    private final String channelName;
    private DATA_STATE state = DATA_STATE.EMPTY;

    private int oldestMessageId;
    private int latestMessageId;
    private List<UUID> listeners;

    public ChannelTuple(String channelName) {
        this.channelName = channelName;
        oldestMessageId = 0;
        latestMessageId = -1;
        listeners = new ArrayList<UUID>();
        state = DATA_STATE.FULL;
    }

    public ChannelTuple(String channelName, int oldestMessageId, int latestMessageId, List<UUID> listeners) {
        this.channelName = channelName;
        this.oldestMessageId = oldestMessageId;
        this.latestMessageId = latestMessageId;
        this.listeners = listeners;
        state = DATA_STATE.FULL;
    }

    @Override
    public ChannelTuple parseTupleData(String[] tupleData) throws IllegalArgumentException {
        String dataChannelName;
        int dataFirstMessageId;
        int dataLastMessageId;

        if (tupleData.length != 4) throw new IllegalArgumentException();
        dataChannelName = tupleData[1];
        String[] firstAndLastMsgId = tupleData[2].split(SEPARATOR);
        if (firstAndLastMsgId.length != 2) throw new IllegalArgumentException();
        dataFirstMessageId = Integer.parseInt(firstAndLastMsgId[0]);
        dataLastMessageId = Integer.parseInt(firstAndLastMsgId[1]);
        String[] listenerIds = tupleData[3].split(SEPARATOR);
        List<UUID> dataListeners = new ArrayList<UUID>();
        if(listenerIds.length > 1 || !listenerIds[0].isEmpty()){
            for(String idStr : listenerIds){
                dataListeners.add(UUID.fromString(idStr));
            }
        }
        return new ChannelTuple(dataChannelName, dataFirstMessageId, dataLastMessageId, dataListeners);
    }

    @Override
    public String[] getAsTemplate() {
        return new String[]{
                TUPLE_PREFIX,
                channelName,
                null,
                null};
    }

    @Override
    public String[] getAsData() throws IllegalStateException {
        if (state != DATA_STATE.FULL) throw new IllegalStateException();
        StringBuilder listenersStringBuilder = new StringBuilder();
        if(!listeners.isEmpty()){
            for(UUID listener : listeners){
                listenersStringBuilder.append(listener.toString()).append(SEPARATOR);
            }
            listenersStringBuilder.deleteCharAt(listenersStringBuilder.length() - 1);
        }
        return new String[]{
                TUPLE_PREFIX,
                channelName,
                oldestMessageId + SEPARATOR + latestMessageId,
                listenersStringBuilder.toString()
        };
    }

    @Override
    public DATA_STATE getDataState() {
        return state;
    }

    public int getOldestMessageId() {
        return oldestMessageId;
    }

    public int getLatestMessageId() {
        return latestMessageId;
    }

    public List<UUID> getListeners() {
        return listeners;
    }
}
