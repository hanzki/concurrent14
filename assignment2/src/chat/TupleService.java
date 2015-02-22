package chat;

import tuplespaces.TupleSpace;

/**
 * Created by hanzki on 19.2.2015.
 */
class TupleService {

    /**
     * Removes a tuple from tuplespace that matches the given template.
     * @param ts Tuplespace where data is fetched from
     * @param templateTuple ChatServerTuple to use as template for fetching data
     * @param <T> Type of ChatServerTuple that is fetched from tuplespace
     * @return The tuple removed from the tuplespace
     */
    static <T extends ChatServerTuple<T>> T getTuple(TupleSpace ts, T templateTuple){
        String[] tupleData = ts.get(templateTuple.getAsTemplate());
        return templateTuple.parseTupleData(tupleData);
    }

    /**
     * Reads a tuple from tuplespace that matches the given template but doesn't remove it.
     * @param ts Tuplespace where data is fetched from
     * @param templateTuple ChatServerTuple to use as template for fetching data
     * @param <T> Type of ChatServerTuple that is fetched from tuplespace
     * @return A tuple with data that matches given template
     * */
    static <T extends ChatServerTuple<T>> T readTuple(TupleSpace ts, T templateTuple){
        String[] tupleData = ts.read(templateTuple.getAsTemplate());
        return templateTuple.parseTupleData(tupleData);
    }

    /**
     * Saves a tuple into the given tuplespace.
     * @param ts Tuplespace where data is saved to
     * @param dataTuple ChatServerTuple that is saved into the tuplespace
     */
    static void putTuple(TupleSpace ts, ChatServerTuple<?> dataTuple){
        ts.put(dataTuple.getAsData());
    }
}
