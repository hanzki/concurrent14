package chat;

import tuplespaces.TupleSpace;

public class ChatServer {
	private final TupleSpace tupleSpace;

	public ChatServer(TupleSpace t, int rows, String[] channelNames) {
		tupleSpace = t;
		ServerStatusTuple serverStatusTuple = new ServerStatusTuple(rows, channelNames);
		tupleSpace.put(serverStatusTuple.getAsData());
		for(String channelName : serverStatusTuple.getChannelNames()){
			ChannelStatusTuple channelStatusTuple = new ChannelStatusTuple(channelName);
			tupleSpace.put(channelStatusTuple.getAsData());
		}
	}

	public ChatServer(TupleSpace t) {
		tupleSpace = t;
	}

	public String[] getChannels() {
		ServerStatusTuple statusTuple = readTuple(new ServerStatusTuple());
		return statusTuple.getChannelNames();
	}

	public void writeMessage(String channel, String message) {

	}

	public ChatListener openConnection(String channel) {
		throw new UnsupportedOperationException(); // Implement this.
		// TODO: Implement ChatServer.openConnection(String);
	}

	private <T extends ChatServerTuple<T>> T getTuple(T templateTuple){
		String[] tupleData = tupleSpace.get(templateTuple.getAsTemplate());
		return templateTuple.parseTupleData(tupleData);
	}

	private <T extends ChatServerTuple<T>> T readTuple(T templateTuple){
		String[] tupleData = tupleSpace.read(templateTuple.getAsTemplate());
		return templateTuple.parseTupleData(tupleData);
	}

}
