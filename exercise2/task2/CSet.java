import java.util.HashSet;
import java.util.Set;

public class CSet<T> {

    private final Set<T> set;

    public CSet() {
        this.set = new HashSet<T>();
    }

    public synchronized void put(T obj) {
        set.add(obj);
    }

    public synchronized void remove(T obj) {
        set.remove(obj);
    }

    public synchronized boolean contains(T obj) {
        return set.contains(obj);
    }
}
