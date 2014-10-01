import java.util.Set;
import java.util.HashSet;

public class CSet<T> {

    private final Set<T> set;

    public CSet() {
        this.set = new HashSet<T>();
    }

    public void put(T obj) {
        synchronized(this) {
            set.add(obj);
        }
    }

    public void remove(T obj) {
        synchronized(set) {
            set.remove(obj);
        }
    }

    public synchronized boolean contains(T obj) {
        synchronized(set) {
            return set.contains(obj);
        }
    }
}
