package chat;

/**
 * Created by hanzki on 21.12.2014.
 */
class MessageTuple implements ChatServerTuple<MessageTuple> {
    private final static String TUPLE_PREFIX = "MSG";
    private DATA_STATE state = DATA_STATE.EMPTY;

    private final String channelName;
    private final int messageId;

    private String message;

    public MessageTuple(String channelName, int messageId) {
        this.channelName = channelName;
        this.messageId = messageId;
    }

    public MessageTuple(String channelName, int messageId, String message) {
        this.channelName = channelName;
        this.messageId = messageId;
        this.message = message;
        state = DATA_STATE.FULL;
    }

    @Override
    public MessageTuple parseTupleData(String[] tupleData) throws IllegalArgumentException {
        String dataChannelName;
        int dataMessageId;
        String dataMessage;

        if (tupleData.length != 4) throw new IllegalArgumentException();
        dataChannelName = tupleData[1];
        dataMessageId = Integer.parseInt(tupleData[2]);
        dataMessage = tupleData[3];

        return new MessageTuple(dataChannelName, dataMessageId, dataMessage);
    }

    @Override
    public String[] getAsTemplate() {
        return new String[]{TUPLE_PREFIX, channelName, String.valueOf(messageId), null};
    }

    @Override
    public String[] getAsData() throws IllegalStateException {
        if(state != DATA_STATE.FULL) throw new IllegalStateException();
        return new String[]{TUPLE_PREFIX, channelName, String.valueOf(messageId), message};
    }

    @Override
    public DATA_STATE getDataState() {
        return state;
    }

    public String getMessage() {
        return message;
    }
}
