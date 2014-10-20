package reactor;

import reactorapi.BlockingQueue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BlockingEventQueue<T> implements BlockingQueue<Event<? extends T>> {

    private final int capacity;
    private final Queue<Event<? extends T>> queue;

	public BlockingEventQueue(int capacity) {
        this.capacity = capacity;
        queue = new LinkedList<Event<? extends T>>();
	}

	public int getSize() {
        synchronized (queue){
            return queue.size();
        }
	}

	public int getCapacity() {
        return capacity;
	}

	public Event<? extends T> get() throws InterruptedException {
        synchronized (queue){
            while(queue.isEmpty()){
                queue.wait();
            }
            Event<? extends T> event = queue.poll();
            queue.notifyAll();
            return event;
        }
	}

	public List<Event<? extends T>> getAll() {
        List<Event<? extends T>> events = new ArrayList<Event<? extends T>>();
        synchronized (queue){
            while(!queue.isEmpty()){
                events.add(queue.poll());
            }
            queue.notifyAll();
        }
        return events;
	}

	public void put(Event<? extends T> event) throws InterruptedException {
		synchronized (queue) {
            while(queue.size() >= capacity){
                queue.wait();
            }
            queue.add(event);
            queue.notifyAll();
        }
	}

}