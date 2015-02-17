package chat;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hanzki on 21.12.2014.
 */
class ServerStatusTuple implements ChatServerTuple<ServerStatusTuple> {

    private static final Pattern VALID_CHANNEL_NAME_PATTERN = Pattern.compile("[a-zA-Z]+");
    private final static String DATA_SEPARATOR = ";";
    private static final String TUPLE_PREFIX = "CONF";
    private DATA_STATE state = DATA_STATE.EMPTY;


    private String[] channelNames;
    private int rows;
    private int nodeCount;

    public ServerStatusTuple() {
    }

    public ServerStatusTuple(int rows, String[] channelNames, int nodeCount) throws IllegalArgumentException {
        if (rows < 0) throw new IllegalArgumentException();
        if (nodeCount < 0) throw new IllegalArgumentException();
        for (String name : channelNames) {
            if (!isValidChannelName(name)) throw new IllegalArgumentException();
        }
        this.rows = rows;
        this.channelNames = channelNames;
        this.nodeCount = nodeCount;
        state = DATA_STATE.FULL;
    }

    @Override
    public ServerStatusTuple parseTupleData(String[] tupleData) throws IllegalArgumentException {
        int dataRows;
        String[] dataChannelNames;
        int dataNodeCount;

        if (tupleData.length != 4) throw new IllegalArgumentException();
        dataRows = Integer.parseInt(tupleData[1]);
        dataChannelNames = tupleData[2].split(DATA_SEPARATOR);
        dataNodeCount = Integer.parseInt(tupleData[3]);

        return new ServerStatusTuple(dataRows, dataChannelNames, dataNodeCount);
    }

    @Override
    public String[] getAsTemplate() {
        return new String[]{TUPLE_PREFIX, null, null, null};
    }

    @Override
    public String[] getAsData() {
        if (state != DATA_STATE.FULL) throw new IllegalStateException();
        String rowStr = String.valueOf(rows);
        String channelNamesStr = "";
        for (int i = 0; i < channelNames.length; i++) {
            if (i > 0) channelNamesStr += DATA_SEPARATOR;
            channelNamesStr += channelNames[i];
        }
        String nodeCountStr = String.valueOf(nodeCount);
        return new String[]{TUPLE_PREFIX, rowStr, channelNamesStr, nodeCountStr};
    }

    @Override
    public DATA_STATE getDataState() {
        return state;
    }

    public String[] getChannelNames() throws IllegalStateException {
        if (state != DATA_STATE.FULL) throw new IllegalStateException();
        return channelNames;
    }

    public int getRows() throws IllegalStateException {
        if (state != DATA_STATE.FULL) throw new IllegalStateException();
        return rows;
    }

    public int getNodeCount() throws IllegalStateException {
        if (state != DATA_STATE.FULL) throw new IllegalStateException();
        return nodeCount;
    }

    private boolean isValidChannelName(String name) {
        if (name == null) return false;
        Matcher m = VALID_CHANNEL_NAME_PATTERN.matcher(name);
        return m.matches();
    }
}
