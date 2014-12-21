package chat;

import java.util.LinkedList;
import java.util.Queue;

public class ChatListener {
    int c = 0;
    private final Queue<String> messages;
    private final ChatServer server;
    private final String channel;

    public ChatListener(ChatServer server, String channel) {
        messages = new LinkedList<String>();
        this.server = server;
        this.channel = channel;
    }

    void addMessage(String message){
        synchronized (messages) {
            messages.add(message);
            messages.notify();
        }
    }

    public String getNextMessage() {
        synchronized (messages){
            try {

                while(messages.isEmpty()){
                    messages.wait();
                }
                String message = messages.poll();
                return message;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Unexpected thing happened.");
            }
        }
	}

	public void closeConnection() {
		server.closeConnection(channel, this);
	}

}
