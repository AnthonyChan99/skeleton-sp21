package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    //    Returns true if deque is empty, false otherwise.
    public boolean isEmpty() {
        return size == 0;
    }

    //    Returns the number of items in the deque.
    public int size() {
        return size;
    }

    //    Prints the items in the deque from first to last, separated by a space. Once all the items have been printed, print out a new line.
    public void printDeque() {
        int i = (nextFirst + 1) % items.length;
        int cnt = 0;
        while (cnt < size) {
            System.out.print(items[i].toString() + ' ');
            i = (i + 1) % items.length;
            cnt += 1;
        }
        System.out.println();
    }

    private void resize(int capacity) {
        if (capacity < size) {
            System.out.println("wrong capacity");
            return;
        }
        if (capacity <= 8) capacity = 8;
        T[] newItems = (T[])new Object[capacity];
        int i = (nextFirst + 1) % items.length;
        int j = 0;
        while (j < size) {
            newItems[j] = items[i];
            i = (i + 1) % items.length;
            j += 1;
        }
        items = newItems;
        nextFirst = items.length - 1;
        nextLast = size;
    }

    //    Adds an item of type T to the front of the deque. You can assume that item is never null.
    public void addFirst(T item) {
        if (size >= items.length) resize(2 * size);
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1) % items.length;
        size += 1;
    }

    //    Adds an item of type T to the back of the deque. You can assume that item is never null.
    public void addLast(T item) {
        if (size >= items.length) resize(2 * size);
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size += 1;
    }

    //    Removes and returns the item at the front of the deque. If no such item exists, returns null.
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (size > 8 && size <= items.length / 4) resize(items.length/2);
        T removeItem = items[(nextFirst + 1) % items.length];
        items[(nextFirst + 1) % items.length] = null;
        nextFirst = (nextFirst + 1) % items.length;
        size -= 1;
        return removeItem;
    }

    //    Removes and returns the item at the back of the deque. If no such item exists, returns null.
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (size > 8 && size <= items.length / 4) resize(items.length/2);
        T removeItem = items[(nextLast - 1) % items.length];
        items[(nextLast - 1) % items.length] = null;
        nextLast = (nextLast - 1) % items.length;
        size -= 1;
        return removeItem;
    }

    //    Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth. If no such item exists, returns null. Must not alter the deque!
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return  items[(index + nextFirst + 1)%items.length];
    }

    //    Returns whether or not the parameter o is equal to the Deque. o is considered equal if it is a Deque and if it contains the same contents
    //    (as governed by the generic T’s equals method) in the same order. (ADDED 2/12: You’ll need to use the instance of keywords for this. Read here for more information)
    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        ArrayDeque<T> compared = (ArrayDeque<T>) o;
        int oSize = compared.size;
        if (oSize != this.size) {
            return false;
        }
        int i = (nextFirst + 1) % items.length;
        int j = (compared.nextFirst + 1) % compared.items.length;
        int cnt = 0;
        while (cnt < size) {
            if (items[i] != compared.items[j]) return false;
            i = (i + 1) % items.length;
            j = (j + 1) % compared.items.length;
            cnt += 1;
        }
        return true;
    }
}
