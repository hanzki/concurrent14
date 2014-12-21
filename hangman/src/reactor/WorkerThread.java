package reactor;

import reactorapi.EventHandler;

/**
 * Created by Hanzki on 22.10.2014.
 */
public class WorkerThread<T> extends Thread {
    private final EventHandler<T> handler;
    private final BlockingEventQueue<Object> queue;

    private volatile boolean running;

    public WorkerThread(EventHandler<T> eh, BlockingEventQueue<Object> q) {
        handler = eh;
        queue = q;
    }

    public void run() {
        running = true;
        while (running) {
            try {
                T message = handler.getHandle().read();
                if (running) {
                    queue.put(new Event<T>(message, handler));
                }
                if (message == null) running = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    public void cancelThread() {
        running = false;
    }
}