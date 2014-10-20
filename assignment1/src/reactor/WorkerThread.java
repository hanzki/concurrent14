package reactor;

import reactorapi.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class WorkerThread<T> extends Thread {
	private final EventHandler<T> handler;
	private final BlockingEventQueue<Object> queue;

	// Additional fields are allowed.

	public WorkerThread(EventHandler<T> eh, BlockingEventQueue<Object> q) {
		handler = eh;
		queue = q;
	}

	public void run() {
		while(!interrupted()){
            try {
                T message = handler.getHandle().read();
                queue.put(new Event<T>(message, handler));
                if(message == null) Thread.currentThread().interrupt();
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
	}

	public void cancelThread() {
        throw new NotImplementedException();
    }
}