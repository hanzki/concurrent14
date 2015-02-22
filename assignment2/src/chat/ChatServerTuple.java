package chat;

/**
 * Created by hanzki on 21.12.2014.
 */
interface ChatServerTuple<T extends ChatServerTuple> {

    /**
     * Data State indicates if the tuple object has enough data to use {@link #getAsData()}.
     */
    enum DATA_STATE {
        EMPTY,
        FULL
    }

    /**
     * Parses tuple from String array representation into a ChatServerTuple object.
     * @param tupleData Tuple data in String array
     * @return A new ChatServerTuple object with data
     * @throws IllegalArgumentException if String array can't be parsed into a ChatServerTuple object.
     */
    T parseTupleData(String[] tupleData) throws IllegalArgumentException;

    /**
     * Gives a template of the tuple which can be used to fetch tuple data from tuplespace.
     * @return String array with nulls as placeholder for data
     */
    String[] getAsTemplate();

    /**
     * Gives the tuple in String array form for saving it into tuplespace.
     * @return Tuple data as String array
     * @throws IllegalStateException if tuples data state is not FULL.
     */
    String[] getAsData() throws IllegalStateException;

    DATA_STATE getDataState();
}
