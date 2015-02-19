package chat;

import java.util.UUID;

/**
 * Created by hanzki on 18.2.2015.
 */
class ChatMessageAlertTuple implements ChatServerTuple<ChatMessageAlertTuple>{
    private final static String TUPLE_PREFIX = "MSG_ALERT";
    private DATA_STATE state = DATA_STATE.EMPTY;

    private final UUID listenerId;
    private final int messageId;

    private String message;

    public ChatMessageAlertTuple(UUID listenerId, int messageId) {
        this.listenerId = listenerId;
        this.messageId = messageId;
    }

    public ChatMessageAlertTuple(UUID listenerId, int messageId, String message) {
        this.listenerId = listenerId;
        this.messageId = messageId;
        this.message = message;
        state = DATA_STATE.FULL;
    }

    @Override
    public ChatMessageAlertTuple parseTupleData(String[] tupleData) throws IllegalArgumentException {
        UUID dataListenerId;
        int dataMessageId;
        String dataMessage;

        if (tupleData.length != 4) throw new IllegalArgumentException();
        dataListenerId = UUID.fromString(tupleData[1]);
        dataMessageId = Integer.parseInt(tupleData[2]);
        dataMessage = tupleData[3];

        return new ChatMessageAlertTuple(dataListenerId, dataMessageId, dataMessage);
    }

    @Override
    public String[] getAsTemplate() {
        return new String[]{
                TUPLE_PREFIX,
                listenerId.toString(),
                String.valueOf(messageId),
                null
        };
    }

    @Override
    public String[] getAsData() throws IllegalStateException {
        if(state != DATA_STATE.FULL) throw new IllegalStateException();
        return new String[]{
                TUPLE_PREFIX,
                listenerId.toString(),
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

