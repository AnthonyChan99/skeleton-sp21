package deque;

public class LinkedListDeque<T> {
    private Node sentinel;
    private int size;

    private class Node {
        public T item;
        public Node prev;
        public Node next;

        Node(T item) {
            this.item = item;
        }
    }

    //    Creates an empty linked list deque.
    public LinkedListDeque() {
        size = 0;
        sentinel = new Node(null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
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
        Node p = sentinel.next;
        while (p != sentinel) {
            System.out.print(p.item.toString() + ' ');
            p = p.next;
        }
        System.out.println();
    }

    //    Adds an item of type T to the front of the deque. You can assume that item is never null.
    public void addFirst(T item) {
        Node p = sentinel.next;
        Node addNode = new Node(item);
        addNode.item = item;
        sentinel.next = addNode;
        addNode.next = p;
        p.prev = addNode;
        addNode.prev = sentinel;
        size += 1;
    }

    //    Adds an item of type T to the back of the deque. You can assume that item is never null.
    public void addLast(T item) {
        Node p = sentinel.prev;
        Node addNode = new Node(item);
        sentinel.prev = addNode;
        addNode.prev = p;
        p.next = addNode;
        addNode.next = sentinel;
        size += 1;
    }

    //    Removes and returns the item at the front of the deque. If no such item exists, returns null.
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node removeNode = sentinel.next;
        Node p = removeNode.next;
        removeNode.prev = null;
        removeNode.next = null;
        sentinel.next = p;
        p.prev = sentinel;
        size -= 1;
        return removeNode.item;
    }

    //    Removes and returns the item at the back of the deque. If no such item exists, returns null.
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node removeNode = sentinel.prev;
        Node p = removeNode.prev;
        removeNode.prev = null;
        removeNode.next = null;
        sentinel.prev = p;
        p.next = sentinel;
        size -= 1;
        return removeNode.item;
    }

    //    Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth. If no such item exists, returns null. Must not alter the deque!
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int cnt = 0;
        Node p = sentinel.next;
        while (p != sentinel) {
            if (cnt == index) {
                return p.item;
            }
            p = p.next;
            cnt += 1;
        }
        return null;
    }

    //    Recursively gets the item at the given index, where 0 is the front, 1 is the next item, and so forth. If no such item exists, returns null. Must not alter the deque!
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return helperGetRecursive(index,sentinel.next);
    }

    private T helperGetRecursive(int left, Node current) {
        if (left == 0) {
            return current.item;
        }
        return helperGetRecursive(left - 1, current.next);
    }

    //    Returns whether or not the parameter o is equal to the Deque. o is considered equal if it is a Deque and if it contains the same contents
    //    (as governed by the generic T’s equals method) in the same order. (ADDED 2/12: You’ll need to use the instance of keywords for this. Read here for more information)
    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        LinkedListDeque<T> compared = (LinkedListDeque<T>) o;
        int oSize = compared.size;
        if (oSize != this.size) {
            return false;
        }
        Node p1 = this.sentinel.next;
        Node p2 = compared.sentinel.next;
        while (p1 != this.sentinel) {
            if (p1.item != p2.item) {
                return false;
            }
            p1 = p1.next;
            p2 = p2.next;
        }
        return true;
    }
}
