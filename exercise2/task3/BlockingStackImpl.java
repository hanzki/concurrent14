import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hanzki on 2.10.2014.
 */
public class BlockingStackImpl<T> implements BlockingStack<T> {

    private final int maxSize;
    private final Stack<T> stack = new Stack<T>();

    /**
     * Thread safe blocking stack implementation.
     *
     * @param maxSize Maximum size of the stack. Requires a positive non-zero value
     * @throws java.lang.IllegalArgumentException if maxSize is negative or zero
     */
    public BlockingStackImpl(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("A non positive maximum size given");
        }
        this.maxSize = maxSize;
    }

    @Override
    public synchronized void push(T object) {
        try {
            while (stack.size() >= maxSize) {
                wait();
            }
            stack.push(object);
            notifyAll();
        } catch (InterruptedException e) {
        }
    }

    @Override
    public synchronized T pop() {
        try {
            while (stack.isEmpty()) {
                wait();
            }
            T popped = stack.pop();
            notifyAll();
            return popped;
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public synchronized int size() {
        return stack.size();
    }

    public static void main(String[] args) throws Exception {
        final BlockingStack<String> bs = new BlockingStackImpl<String>(3);
        ExecutorService es = Executors.newFixedThreadPool(4);

        class RPush implements Runnable {
            private final BlockingStack<String> bs;
            private String s;

            RPush(BlockingStack<String> bs, String s) {
                this.bs = bs;
                this.s = s;
            }

            @Override
            public void run() {
                synchronized (bs) {
                    System.out.println("pushing " + s + " size is " + bs.size());
                    bs.push(s);
                    System.out.println("pushed " + s + " size is " + bs.size());
                }
            }
        }
        ;

        class RPop implements Runnable {
            private final BlockingStack<String> bs;

            RPop(BlockingStack<String> bs) {
                this.bs = bs;
            }

            @Override
            public void run() {
                synchronized (bs) {
                    System.out.println("popping... size is " + bs.size());
                    String s = bs.pop();
                    System.out.println("popped " + s + " size is " + bs.size());
                }
            }
        }
        ;

        es.execute(new RPush(bs, "first"));
        es.execute(new RPush(bs, "second"));
        es.execute(new RPush(bs, "third"));
        es.execute(new RPush(bs, "fourth"));
        es.execute(new RPop(bs));
        es.execute(new RPop(bs));
        es.execute(new RPush(bs, "fifth"));
        es.execute(new RPop(bs));
        es.execute(new RPop(bs));
        es.execute(new RPop(bs));
        es.execute(new RPop(bs));
        es.execute(new RPush(bs, "sixth"));

        es.shutdown();
        if (es.awaitTermination(10000L, TimeUnit.MILLISECONDS)) {
            System.out.println("done");
            System.exit(0);
        } else {
            System.out.println("error");
            System.exit(1);
        }
    }
}
