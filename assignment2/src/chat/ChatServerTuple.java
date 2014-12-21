package chat;

/**
 * Created by hanzki on 21.12.2014.
 */
interface ChatServerTuple<T extends ChatServerTuple> {

    enum DATA_STATE {
        EMPTY,
        FULL
    }

    final static String CHAT_SERVER_NAME = "chatserver";

    T parseTupleData(String[] tupleData) throws IllegalArgumentException;

    String[] getAsTemplate();

    String[] getAsData() throws IllegalStateException;

    DATA_STATE getDataState();
}
