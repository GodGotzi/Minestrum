package at.gotzi.minestrum.api;

import java.util.Arrays;
import java.util.stream.Stream;

public class SizedList<T> {
    
    private T[] array;
    
    private int currentSize = 0;
    
    public SizedList(T[] sizedArray) {
        this.array = sizedArray;
    }

    public int size() {
        return currentSize;
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    public boolean contains(Object o) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(o)) {
                return true;
            }
        }

        return false;
    }

    public Stream<T> stream() {
        return Arrays.stream(array);
    }

    public int add(T t) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                array[i] = t;
                ++currentSize;
                return i;
            }
        }

        return -1;
    }

    public boolean remove(Object t) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(t)) {
                array[i] = null;
                --currentSize;
                return true;
            }
        }

        return false;
    }

    public void clear() {
        for (int i = 0; i < array.length; i++)
            array[i] = null;
    }
    
    public int getMaxSize() {
        return array.length;
    }
}
