
public class Counter {

    private int value;

    public Counter(int n) {
        this.value = n;
    }

    public synchronized int incrementAndGet() {
        return ++value;
    }

    public synchronized int decrementAndGet() {
        return --value;
    }

}