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

    public ServerStatusTuple() {
    }

    public ServerStatusTuple(int rows, String[] channelNames) throws IllegalArgumentException {
        if (rows < 0) throw new IllegalArgumentException();
        for (String name : channelNames) {
            if (!isValidChannelName(name)) throw new IllegalArgumentException();
        }
        this.rows = rows;
        this.channelNames = channelNames;
        state = DATA_STATE.FULL;
    }

    @Override
    public ServerStatusTuple parseTupleData(String[] tupleData) throws IllegalArgumentException {
        int dataRows;
        String[] dataChannelNames;

        if (tupleData.length != 3) throw new IllegalArgumentException();
        dataRows = Integer.parseInt(tupleData[1]);
        dataChannelNames = tupleData[2].split(DATA_SEPARATOR);

        return new ServerStatusTuple(dataRows, dataChannelNames);
    }

    @Override
    public String[] getAsTemplate() {
        return new String[]{TUPLE_PREFIX, null, null};
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
        return new String[]{TUPLE_PREFIX, rowStr, channelNamesStr};
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

    private boolean isValidChannelName(String name) {
        if (name == null) return false;
        Matcher m = VALID_CHANNEL_NAME_PATTERN.matcher(name);
        return m.matches();
    }
}
