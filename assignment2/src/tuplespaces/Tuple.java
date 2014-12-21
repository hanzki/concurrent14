package tuplespaces;

import java.util.Arrays;

/**
 * Created by hanzki on 21.12.2014.
 */
public class Tuple {

    private String[] items;

    public Tuple(String[] items) {
        this.items = Arrays.copyOf(items, items.length);
    }

    public String[] getItems() {
        return Arrays.copyOf(items, items.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple tuple = (Tuple) o;

        if(items.length != tuple.items.length) return false;

        for(int i = 0; i < items.length; i++){
            if( items[i] != null &&
                tuple.items[i] != null &&
                ! items[i].equals(tuple.items[i])
            ) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return items.length;
    }
}
