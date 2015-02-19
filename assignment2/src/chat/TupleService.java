package chat;

import tuplespaces.TupleSpace;

/**
 * Created by hanzki on 19.2.2015.
 */
class TupleService {
    static <T extends ChatServerTuple<T>> T getTuple(TupleSpace ts, T templateTuple){
        String[] tupleData = ts.get(templateTuple.getAsTemplate());
        return templateTuple.parseTupleData(tupleData);
    }

    static <T extends ChatServerTuple<T>> T readTuple(TupleSpace ts, T templateTuple){
        String[] tupleData = ts.read(templateTuple.getAsTemplate());
        return templateTuple.parseTupleData(tupleData);
    }

    static void putTuple(TupleSpace ts, ChatServerTuple<?> dataTuple){
        ts.put(dataTuple.getAsData());
    }
}
