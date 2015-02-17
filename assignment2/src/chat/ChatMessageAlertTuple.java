package chat;

/**
 * Created by hanzki on 18.2.2015.
 */
class ChatMessageAlertTuple implements ChatServerTuple<ChatMessageAlertTuple>{
    private final static String TUPLE_PREFIX = "MSG_ALERT";
    private DATA_STATE state = DATA_STATE.EMPTY;

    private final String channelName;
    private final int messageId;
    private final int nodeId;

    private String message;

    public ChatMessageAlertTuple(int nodeId, String channelName, int messageId) {
        this.nodeId = nodeId;
        this.channelName = channelName;
        this.messageId = messageId;
    }

    public ChatMessageAlertTuple(int nodeId, String channelName, int messageId, String message) {
        this.nodeId = nodeId;
        this.channelName = channelName;
        this.messageId = messageId;
        this.message = message;
        state = DATA_STATE.FULL;
    }

    @Override
    public ChatMessageAlertTuple parseTupleData(String[] tupleData) throws IllegalArgumentException {
        String dataChannelName;
        int dataMessageId;
        int dataNodeId;
        String dataMessage;

        if (tupleData.length != 5) throw new IllegalArgumentException();
        dataNodeId = Integer.parseInt(tupleData[1]);
        dataChannelName = tupleData[2];
        dataMessageId = Integer.parseInt(tupleData[3]);
        dataMessage = tupleData[4];

        return new ChatMessageAlertTuple(dataNodeId, dataChannelName, dataMessageId, dataMessage);
    }

    @Override
    public String[] getAsTemplate() {
        return new String[]{
                TUPLE_PREFIX,
                String.valueOf(nodeId),
                channelName,
                String.valueOf(messageId),
                null
        };
    }

    @Override
    public String[] getAsData() throws IllegalStateException {
        if(state != DATA_STATE.FULL) throw new IllegalStateException();
        return new String[]{
                TUPLE_PREFIX,
                String.valueOf(nodeId),
                channelName,
                String.valueOf(messageId),
                message
        };
    }

    @Override
    public DATA_STATE getDataState() {
        return state;
    }

    public String getMessage() {
        return message;
    }
}

