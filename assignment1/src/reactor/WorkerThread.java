package reactor;

import reactorapi.EventHandler;

public class WorkerThread<T> extends Thread {
    private final EventHandler<T> handler;
    private final BlockingEventQueue<Object> queue;

    private volatile boolean running;

    // Additional fields are allowed.

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
                running = false;
            }
        }
    }

    public void cancelThread() {
        running = false;
    }
}