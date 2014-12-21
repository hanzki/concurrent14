package chat;

/**
 * Created by hanzki on 21.12.2014.
 */
class ChannelStatusTuple implements ChatServerTuple<ChannelStatusTuple> {
    private static final String ID_SEPARATOR = ":";
    private final String channelName;
    private DATA_STATE state = DATA_STATE.EMPTY;

    private int firstMessageId;
    private int lastMessageId;

    public ChannelStatusTuple(String channelName) {
        this.channelName = channelName;
        firstMessageId = -1;
        lastMessageId = 0;
        state = DATA_STATE.FULL;
    }

    public ChannelStatusTuple(String channelName, int firstMessageId, int lastMessageId) {
        this.channelName = channelName;
        this.firstMessageId = firstMessageId;
        this.lastMessageId = lastMessageId;
        state = DATA_STATE.FULL;
    }

    public ChannelStatusTuple(String[] tupleData) throws IllegalArgumentException {
        if (tupleData.length != 3) throw new IllegalArgumentException();
        channelName = tupleData[1];
        String[] dataParts = tupleData[2].split(ID_SEPARATOR);
        if (dataParts.length != 2) throw new IllegalArgumentException();
        firstMessageId = Integer.parseInt(dataParts[0]);
        lastMessageId = Integer.parseInt(dataParts[1]);
        state = DATA_STATE.FULL;
    }

    @Override
    public ChannelStatusTuple parseTupleData(String[] tupleData) throws IllegalArgumentException {
        String dataChannelName;
        int dataFirstMessageId;
        int dataLastMessageId;

        if (tupleData.length != 3) throw new IllegalArgumentException();
        dataChannelName = tupleData[1];
        String[] dataParts = tupleData[2].split(ID_SEPARATOR);
        if (dataParts.length != 2) throw new IllegalArgumentException();
        dataFirstMessageId = Integer.parseInt(dataParts[0]);
        dataLastMessageId = Integer.parseInt(dataParts[1]);

        return new ChannelStatusTuple(dataChannelName,dataFirstMessageId,dataLastMessageId);
    }

    @Override
    public String[] getAsTemplate() {
        return new String[]{
                CHAT_SERVER_NAME,
                channelName,
                null};
    }

    @Override
    public String[] getAsData() throws IllegalStateException {
        if (state != DATA_STATE.FULL) throw new IllegalStateException();
        return new String[]{
                CHAT_SERVER_NAME,
                channelName,
                firstMessageId + ID_SEPARATOR + lastMessageId};
    }

    @Override
    public DATA_STATE getDataState() {
        return state;
    }

    public int getFirstMessageId() {
        return firstMessageId;
    }

    public int getLastMessageId() {
        return lastMessageId;
    }

}
