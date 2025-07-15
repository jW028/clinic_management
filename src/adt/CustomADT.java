package adt;

public class CustomADT {
    private static class Node {
        Object data;
        Node next;

        public Node(Object data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public CustomADT() {
        head = null;
        tail = null;
        size = 0;
    }

    // Queue Operations
    public void enqueue(Object item) {
        Node newNode = new Node(item);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public Object dequeue() {
        if (head == null) return null;
        Object data = head.data;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return data;
    }

    // List Operations
    public void add(Object item) {
        enqueue(item); // Using same underlying structure
    }

    public Object get(int index) {
        if (index < 0 || index >= size) return null;
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    // Utility Operations
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    public boolean contains(Object item) {
        Node current = head;
        while (current != null) {
            if (current.data.equals(item)) return true;
            current = current.next;
        }
        return false;
    }
}