package adt;

<<<<<<< HEAD
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
=======
/**
 * A versatile Abstract Data Type that combines the functionality of:
 * - Map (HashMap-like key-value storage with O(1) average lookup)
 * - List (Ordered collection with indexed access)
 * - Queue (FIFO operations)
 * - Stack (LIFO operations)
 * 
 * This ADT is designed to be generic and suitable for use across all modules
 * in the clinic management system.
 * 
 *                                       |
 *                                       |
 *                                       v
 * 
 * --------> AS REFERENCE FOR NOW: WE CAN BASE OUR ADT OFF THIS STRUCTURE <-----------
 * 
 * 
 * 
 */
public class CustomADT<K, V> {
    
    // Internal node structure for the doubly-linked list
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    // Hash table for O(1) key-based lookups
    private Node<K, V>[] table;
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    private int capacity;
    private int size;
    
    // Doubly-linked list for maintaining insertion order and enabling list operations
    private Node<K, V> head;
    private Node<K, V> tail;
    
    // Constructor
    @SuppressWarnings("unchecked")
    public CustomADT() {
        this.capacity = DEFAULT_CAPACITY;
        this.table = new Node[capacity];
        this.size = 0;
        this.head = null;
        this.tail = null;
    }
    
    // ===============================
    // CORE UTILITY METHODS
    // ===============================
    
    private int hash(K key) {
        if (key == null) return 0;
        return Math.abs(key.hashCode() % capacity);
    }
    
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        capacity *= 2;
        table = new Node[capacity];
        
        // Rehash all existing entries
        for (Node<K, V> node : oldTable) {
            while (node != null) {
                Node<K, V> next = node.next;
                int newIndex = hash(node.key);
                node.next = table[newIndex];
                table[newIndex] = node;
                node = next;
            }
        }
    }
    
    private void updateIndices() {
        // This method is no longer needed since we removed the index field
        // but we'll keep it as a placeholder for future enhancements
    }
    
    // ===============================
    // MAP OPERATIONS
    // ===============================
    
    /**
     * Associates the specified value with the specified key
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no mapping
     */
    public V put(K key, V value) {
        int index = hash(key);
        Node<K, V> current = table[index];
        
        // Check if key already exists
        while (current != null) {
            if (current.key != null && current.key.equals(key)) {
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            current = current.next;
        }
        
        // Create new node
        Node<K, V> newNode = new Node<>(key, value);
        
        // Add to hash table
        newNode.next = table[index];
        table[index] = newNode;
        
        // Add to linked list (at tail for insertion order)
>>>>>>> 6f518f0c9ce339547ac6dcb17c22c815185d649c
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
<<<<<<< HEAD
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
=======
            newNode.prev = tail;
            tail = newNode;
        }
        
        size++;
        
        // Resize if necessary
        if (size > capacity * LOAD_FACTOR) {
            resize();
        }
        
        return null;
    }
    
    /**
     * Returns the value to which the specified key is mapped
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if no mapping exists
     */
    public V get(K key) {
        int index = hash(key);
        Node<K, V> current = table[index];
        
        while (current != null) {
            if (current.key != null && current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }
    
    /**
     * Returns true if this map contains a mapping for the specified key
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }
    
    /**
     * Removes the mapping for the specified key from this map if present
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping
     */
    public V remove(K key) {
        int index = hash(key);
        Node<K, V> current = table[index];
        Node<K, V> prev = null;
        
        while (current != null) {
            if (current.key != null && current.key.equals(key)) {
                // Remove from hash table
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                
                // Remove from linked list
                removeFromList(current);
                
                size--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }
    
    // ===============================
    // LIST OPERATIONS
    // ===============================
    
    /**
     * Returns the element at the specified position in this list
     * @param index index of the element to return
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public V get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        Node<K, V> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }
    
    /**
     * Replaces the element at the specified position with the specified element
     * @param index index of the element to replace
     * @param value element to be stored at the specified position
     * @return the element previously at the specified position
     */
    public V set(int index, V value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        Node<K, V> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        V oldValue = current.value;
        current.value = value;
        return oldValue;
    }
    
    /**
     * Inserts the specified element at the specified position
     * @param index index at which the specified element is to be inserted
     * @param key the key for the element
     * @param value element to be inserted
     */
    public void add(int index, K key, V value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        if (index == size) {
            put(key, value);
            return;
        }
        
        Node<K, V> newNode = new Node<>(key, value);
        
        if (index == 0) {
            newNode.next = head;
            if (head != null) {
                head.prev = newNode;
            }
            head = newNode;
            if (tail == null) {
                tail = newNode;
            }
        } else {
            Node<K, V> current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            
            newNode.next = current;
            newNode.prev = current.prev;
            current.prev.next = newNode;
            current.prev = newNode;
        }
        
        // Add to hash table
        int hashIndex = hash(key);
        Node<K, V> hashNext = table[hashIndex];
        table[hashIndex] = newNode;
        newNode.next = hashNext;
        
        size++;
        updateIndices();
    }
    
    /**
     * Removes the element at the specified position
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     */
    public V removeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        Node<K, V> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        
        return remove(current.key);
    }
    
    // ===============================
    // QUEUE OPERATIONS (FIFO)
    // ===============================
    
    /**
     * Inserts the specified element into this queue (at the tail)
     * @param key the key for the element
     * @param value the element to add
     * @return true if the element was added successfully
     */
    public boolean offer(K key, V value) {
        put(key, value);
        return true;
    }
    
    /**
     * Retrieves and removes the head of this queue, or returns null if empty
     * @return the head of this queue, or null if this queue is empty
     */
    public V poll() {
        if (isEmpty()) {
            return null;
        }
        V value = head.value;
        remove(head.key);
        return value;
    }
    
    /**
     * Retrieves, but does not remove, the head of this queue, or returns null if empty
     * @return the head of this queue, or null if this queue is empty
     */
    public V peek() {
        return isEmpty() ? null : head.value;
    }
    
    // ===============================
    // STACK OPERATIONS (LIFO)
    // ===============================
    
    /**
     * Pushes an element onto the top of this stack (at the tail)
     * @param key the key for the element
     * @param value the element to be pushed onto this stack
     */
    public void push(K key, V value) {
        put(key, value);
    }
    
    /**
     * Removes the object at the top of this stack and returns that object
     * @return the object at the top of this stack
     * @throws RuntimeException if this stack is empty
     */
    public V pop() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        V value = tail.value;
        remove(tail.key);
        return value;
    }
    
    /**
     * Looks at the object at the top of this stack without removing it
     * @return the object at the top of this stack
     * @throws RuntimeException if this stack is empty
     */
    public V top() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        return tail.value;
    }
    
    // ===============================
    // UTILITY METHODS
    // ===============================
    
    private void removeFromList(Node<K, V> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
         
        updateIndices();
    }
    
    /**
     * Returns the number of elements in this collection
     * @return the number of elements in this collection
     */
    public int size() {
        return size;
    }
    
    /**
     * Returns true if this collection contains no elements
     * @return true if this collection contains no elements
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Removes all elements from this collection
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        table = new Node[DEFAULT_CAPACITY];
        capacity = DEFAULT_CAPACITY;
        head = tail = null;
        size = 0;
    }
    
    /**
     * Returns an array of all values in insertion order
     * @return array of values
     */
    @SuppressWarnings("unchecked")
    public V[] toArray() {
        V[] array = (V[]) new Object[size];
        Node<K, V> current = head;
        int index = 0;
        while (current != null) {
            array[index++] = current.value;
            current = current.next;
        }
        return array;
    }
    
    /**
     * Applies the given operation to each value in this collection
     * @param operation the operation to apply to each value
     */
    public void forEach(ValueProcessor<V> operation) {
        Node<K, V> current = head;
        while (current != null) {
            operation.process(current.value);
            current = current.next;
        }
    }
    
    /**
     * Functional interface for processing values in forEach method
     */
    public interface ValueProcessor<V> {
        void process(V value);
    }
    
    /**
     * Returns a string representation of this collection
     * @return a string representation of this collection
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node<K, V> current = head;
        while (current != null) {
            sb.append(current.value);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
>>>>>>> 6f518f0c9ce339547ac6dcb17c22c815185d649c
