package reactor;

import reactorapi.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hanzki on 22.10.2014.
 */
public class Dispatcher {

    private final BlockingEventQueue<Object> eventQueue;
    private final Map<EventHandler<?>, WorkerThread<?>> handlers;

	public Dispatcher() {
		this(10);
	}

	public Dispatcher(int capacity) {
		eventQueue = new BlockingEventQueue<Object>(capacity);
        handlers = new HashMap<EventHandler<?>, WorkerThread<?>>();
	}

	public void handleEvents() throws InterruptedException {
		while(!handlers.isEmpty()){
            Event<?> event = select();
            if(event.getEvent() == null) removeHandler(event.getHandler());
            event.handle();
        }
	}

	public Event<?> select() throws InterruptedException {
		return eventQueue.get();
	}

	public <T> void addHandler(EventHandler<T> h) {
		WorkerThread<T> thread = new WorkerThread<T>(h,eventQueue);
        handlers.put(h, thread);
        thread.start();
	}

	public void removeHandler(EventHandler<?> h) {
		if(handlers.containsKey(h)){
            handlers.get(h).cancelThread();
            handlers.get(h).interrupt();
            handlers.remove(h);
        }
	}

}
