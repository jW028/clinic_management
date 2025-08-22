package adt;

import java.io.Serializable;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
 * @param <K> the type of keys maintained by this ADT
 * @param <V> the type of mapped values
 */
public class CustomADT<K, V> implements CustomADTInterface<K, V>, Iterable<V>, Serializable {
    private static final long serialVersionUID = 1L;

    // Internal node structure for the doubly-linked list
    private static class Node<K, V> implements Serializable {
        private static final long serialVersionUID = 1L;

        K key;
        V value;
        Node<K, V> prev;     // Doubly-linked list (order) pointer
        Node<K, V> next;     // Doubly-linked list (order) pointer
        Node<K, V> hashNext; // Hash table chain (bucket) pointer

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

        int hash = key.hashCode();
        hash ^= (hash >>> 16);
        return Math.abs(hash % capacity);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        capacity *= 2;
        Node<K, V>[] newTable = new Node[capacity];
        table = newTable;
        
        
        // Rehash all entries
        Node<K, V> current = head;
        while (current != null) {
            // Clear old hash pointer
            current.hashNext = null;

            // Rehash and reinsert into hash table
            int newIndex = hash(current.key);
            current.hashNext = table[newIndex];
            table[newIndex] = current;

            current = current.next;
        }
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
    @Override
    public V put(K key, V value) {
        int index = hash(key);
        Node<K, V> current = table[index];

        // Check if key already exists by traversing the hash chain
        while (current != null) {
            if (current.key == key || (current.key != null && current.key.equals(key))) {
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            current = current.hashNext; // use hashNext for hash traversal
        }

        // Create new node
        Node<K, V> newNode = new Node<>(key, value);
        
        // Add to hash table
        newNode.hashNext = table[index];
        table[index] = newNode;

        // Add to linked list (at tail for insertion order)
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
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
    @Override
    public V get(K key) {
        int index = hash(key);
        Node<K, V> current = table[index];

        // Traverse the hash chain
        while (current != null) {
            if (current.key == key || (current.key != null && current.key.equals(key))) {
                return current.value;
            }
            current = current.hashNext;
        }
        return null;
    }

    /**
     * Returns true if this map contains a mapping for the specified key
     */
    @Override
    public boolean containsKey(K key) {
        if (key == null) return false;

        int index = hash(key);
        Node<K, V> current = table[index];

        while (current != null) {
            if (current.key != null && current.key.equals(key)) {
                return true; 
            }
            current = current.hashNext;
        }
        return false;
    }

    /**
     * Removes the mapping for the specified key from this map if present
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping
     */
    @Override
    public V remove(K key) {
        int index = hash(key);
        Node<K, V> current = table[index];
        Node<K, V> hashPrev = null;
        
        while (current != null) {
            if (current.key != null && current.key.equals(key)) {
                // Remove from hash table
                if (hashPrev == null) {
                    table[index] = current.hashNext;
                } else {
                    hashPrev.hashNext = current.hashNext;
                }

                // Remove from linked list
                removeFromList(current);

                size--;
                return current.value;
            }
            hashPrev = current;
            current = current.hashNext; // Use hashNext for hash traversal
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
    @Override
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
    @Override
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
    @Override
    public void add(int index, K key, V value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        // Check for duplicate key 
        if (containsKey(key)) {
            throw new IllegalArgumentException("Key already exists: " + key);
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
            // Traverse to the node currently at the target index
            for (int i = 0; i < index; i++) {
                current = current.next;
            }

            // Insert newNode before current
            newNode.next = current;
            newNode.prev = current.prev;
            if (current.prev != null) {
                current.prev.next = newNode;
            }
            current.prev = newNode;
        }

        // Add to hash table
        int hashIndex = hash(key);
        newNode.hashNext = table[hashIndex];
        table[hashIndex] = newNode;
        
        size++;

        // Resize if necessary
        if (size > capacity * LOAD_FACTOR) {
            resize();
        }
    }

    /**
     * Removes the element at the specified position
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     */
    @Override
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
     * Inserts the specified element into this queue.
     * This is the standard, map-aware queue operation.
     * @return true (as specified by the Queue interface)
     */
    @Override
    public boolean offer(K key, V value) {
        put(key, value);
        return true;
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if empty
     * This is the standard, map-aware queue operation.
     * @return the head of this queue, or null if this queue is empty
     */
    @Override
    public V poll() {
        if (isEmpty()) {
            return null;
        }
        // remove() will handle removing from both the list and the map
        return remove(head.key);
    }

    /**
     * Retrieves, but does not remove, the head of this queue, or returns null if empty
     * @return the head of this queue, or null if this queue is empty
     */
    @Override
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
    @Override
    public void push(K key, V value) {
        put(key, value);
    }

    /**
     * Removes the object at the top of this stack and returns that object
     * @return the object at the top of this stack
     * @throws RuntimeException if this stack is empty
     */
    @Override
    public V pop() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        // remove() will handle removing from both the list and the map
        return remove(tail.key);
    }

    /**
     * Looks at the object at the top of this stack without removing it
     * @return the object at the top of this stack
     * @throws RuntimeException if this stack is empty
     */
    @Override
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
            // Node is the head
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            // Node is the tail
            tail = node.prev;
        }
    }

    /**
     * Returns the number of elements in this collection
     * @return the number of elements in this collection
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns true if this collection contains no elements
     * @return true if this collection contains no elements
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Removes all elements from this collection
     */
    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        // Clear the hash table
        this.capacity = DEFAULT_CAPACITY;
        this.table = new Node[capacity];
        // Clear the linked list
        this.head = this.tail = null;
        this.size = 0;
    }

    /**
     * Returns an array of all values in insertion order
     * @return array of values
     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public V[] toArray() {
//        V[] array = (V[]) new Object[size];
//        Node<K, V> current = head;
//        int index = 0;
//        while (current != null) {
//            array[index++] = current.value;
//            current = current.next;
//        }
//        return array;
//    }

    @SuppressWarnings("unchecked")
    @Override
    public V[] toArray(V[] array) {
        int currentSize = size();
        if (array.length < size) {
            array = (V[])java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), size);
        }
        Node<K,V> current = head;
        int i = 0;
        while (current != null) {
            array[i++] = current.value;
            current = current.next;
        }
        return array;
    }

    /**
     * Returns an iterator over the values in this collection in insertion order
     * @return an Iterator over the values in this collection
     */
    @Override 
    public Iterator<V> iterator() {
        return new CustomADTIterator();
    }

    private class CustomADTIterator implements Iterator<V> {
        private Node<K, V> current;
        private Node<K, V> lastReturned;
        private int expectedSize;

        public CustomADTIterator() {
            this.current = head;
            this.lastReturned = null;
            this.expectedSize = size; // Capture the expected size for fail-fast behavior
        }

        /**
         * Returns true if the iteration has more elements
         */
        @Override 
        public boolean hasNext() {
            checkForConcurrentModification();
            return current != null;
        }

        /**
         * Returns the next element in the iteration
         */
        @Override
        public V next() {
            checkForConcurrentModification();
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in iteration");
            }

            lastReturned = current;
            current = current.next;
            return lastReturned.value;
        }

        /**
         * Removes the last element returned by this iterator
         */
        @Override
        public void remove() {
            checkForConcurrentModification();
            if (lastReturned == null) {
                throw new IllegalStateException("No element to remove");
            }

            // Remove the last returned element
            CustomADT.this.remove(lastReturned.key);
            expectedSize = size; // Update expected size after removal
            lastReturned = null; 
        }

        /**
         * Check if the collection has been modified during iteration
         */
        private void checkForConcurrentModification() {
            if (expectedSize != size) {
                throw new ConcurrentModificationException("Collection modified during iteration");
            }
        }
    }


    @SuppressWarnings("unchecked")
    private Node<K,V>[] createNodeArray() {
        Node<K, V>[] array = new Node[size];
        Node<K, V> current = head;
        int index = 0;

        while (current != null) {
            array[index++] = current;
            current = current.next;
        }
        return array;
    }

    /**
     * Linear search through the collection
     * @param target the value to search for
     * @param comparator the comparator for equality checking
     * @return the first matching value, or null if not found
     */
    public V linearSearch(V target, Comparator<V> comparator) {
        Node<K, V> current = head;
        while (current != null) {
            if (comparator.compare(current.value, target) == 0) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * Binary search for sorted data 
     * Requires data to be sorted first
     */
    public V binarySearch(V target, Comparator<V> comparator) {
        if (isEmpty() || target == null || comparator == null) return null;

        // Convert linked list to array for binary search 
        Node<K, V>[] nodeArray = createNodeArray();

        int left = 0;
        int right = size - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nodeArray[mid].value == null) {
                // Handle null values in array
                left = mid + 1;
                continue;
            }

            int comparison = comparator.compare(nodeArray[mid].value, target);

            if (comparison == 0) {
                return nodeArray[mid].value;
            } else if (comparison < 0) {
                // Target value is greater than mid, search the right half
                left = mid + 1;
            } else {
                // Target value is less than mid, search the left half
                right = mid - 1;
            }
        }
        return null;
    }

    /**
     * Sort the collection using merge sort algorithm
     * @param comparator the comparator to determine the order of elements
     */
    public void sort(Comparator<V> comparator) {
        if (size <= 1) return;

        Node<K, V>[] nodeArray = createNodeArray();
        mergeSort(nodeArray, 0, size - 1, comparator);
        rebuildLinkedList(nodeArray);
    }

    private void mergeSort(Node<K, V>[] array, int left, int right, Comparator<V> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            mergeSort(array, left, mid, comparator);
            mergeSort(array, mid + 1, right, comparator);
            merge(array, left, mid, right, comparator);
        }
    }

    @SuppressWarnings("unchecked")
    private void merge(Node<K, V>[] array, int left, int mid, int right, Comparator<V> comparator) {
        int leftSize = mid - left + 1;
        int rightSize = right - mid;

        Node <K, V>[] leftArray = new Node[leftSize];
        Node <K, V>[] rightArray = new Node[rightSize];

        // Copy data to temporary arrays
        System.arraycopy(array, left, leftArray, 0, leftSize);
        System.arraycopy(array, mid + 1, rightArray, 0, rightSize);

        int i = 0, j = 0, k = left;

        // Merge back temporary arrays
        while (i < leftSize && j < rightSize) {
            if (comparator.compare(leftArray[i].value,  rightArray[j].value) <= 0) {
                array[k] = leftArray[i];
                i++;
            } else {
                array[k] = rightArray[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements
        while (i < leftSize) {
            array[k] = leftArray[i];
            i++;
            k++;
        }

        while (j < rightSize) {
            array[k] = rightArray[j];
            j++;
            k++;
        }
    }

    /**
     * Rebuild the linked list from sorted node array
     * @param nodeArray the sorted array of nodes
     */
    private void rebuildLinkedList(Node<K, V>[] nodeArray) {
        if (nodeArray.length == 0) return;

        // Reset all next/previous pointers
        for (Node<K, V> node : nodeArray) {
            node.next = null;
            node.prev = null;
        }

        // Rebuild the linked list
        head = nodeArray[0];
        tail = nodeArray[nodeArray.length - 1];

        for (int i = 0; i < nodeArray.length; i++) {
            if (i > 0) {
                nodeArray[i].prev = nodeArray[i - 1];
                nodeArray[i - 1].next = nodeArray[i];
            }
        }
    }

    /**
     * Find the first element that matches the given target
     * @param target the value to search for
     * @param comparator the comparator for matching
     * @return the first matching element, or null if not found
     */
    public V findFirst(Comparator<V> condition, V referenceValue) {
        Node<K, V> current = head;
        while (current != null) {
            if (condition.compare(current.value, referenceValue) == 0) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * Find all elements that match the given criteria
     * @param target the value to search for
     * @param comparator the comparator for matching
     * @return a new CustomADT containing all elements
     */
    public CustomADT<K, V> findAll(V target, Comparator<V> comparator) {
        CustomADT<K, V> results = new CustomADT<>();
        Node<K, V> current = head;

        while (current != null) {
            if (comparator.compare(current.value, target) == 0) {
                results.put(current.key, current.value);
            }
            current = current.next;
        }
        return results;
    }

    /**
     * Filter elements based on a condition using a predicate-style comparator
     * @param referenceValue the value to compare against
     * @param condition a comparator that returns 0 for elements to keep
     * @return a new CustomADT containing filtered elements
     */
    public CustomADT<K, V> filter(V referenceValue, Comparator<V> condition) {
        CustomADT<K, V> filtered = new CustomADT<>();
        Node<K, V> current = head;

        while (current != null) {
            if (condition.compare(current.value, referenceValue) == 0) {
                // Found a matching element
                filtered.put(current.key, current.value);
            }
            current = current.next;
        }
        return filtered;
    }

    /**
     * Range search for elements within a specified range
     * @param min the minimum value (includsive)
     * @param max the maximum value (inclusive)
     * @param comparator the comparator for range checking
     * @return a new CustomADT containing elements within the range
     */
    public CustomADT<K, V> rangeSearch(V min, V max, Comparator<V> comparator) {
        CustomADT<K, V> results = new CustomADT<>();
        Node<K, V> current = head;

        while (current != null) {
            if (comparator.compare(current.value, min) >= 0 &&
                comparator.compare(current.value, max) <= 0) {
                    results.put(current.key, current.value);
            }
            current = current.next;
        }
        return results;
    }

    /**
     * Check if the collection is sorted according to the given comparator
     * @param comparator the comparator to check ordering
     * @return true if sorted, false otherwise
     */
    public boolean isSorted(Comparator<V> comparator) {
        if (size <= 1) return true;

        Node<K, V> current = head;
        while (current.next != null) {
            if (comparator.compare(current.value, current.next.value) > 0) {
                return false;
            }
            current = current.next;
        }
        return true;
    }

    /**
     * Sort by keys 
     * @param keyComparator comparator for keys
     */
    public void sortByKeys(Comparator<K> keyComparator) {
        if (size <= 1) return;

        Node<K, V>[] nodeArray = createNodeArray();
        mergeSortByKeys(nodeArray, 0, size - 1, keyComparator);
        rebuildLinkedList(nodeArray);
    }

    /**
     * Recursive merge sort for keys
     */
    private void mergeSortByKeys(Node<K,V>[] array, int left, int right, Comparator<K> keyComparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            mergeSortByKeys(array, left, mid, keyComparator);
            mergeSortByKeys(array, mid + 1, right, keyComparator);
            mergeByKeys(array, left, mid, right, keyComparator);
        }
    }

    @SuppressWarnings("unchecked")
    private void mergeByKeys(Node<K, V>[] array, int left, int mid, int right, Comparator<K> keyComparator) {
        int leftSize = mid - left + 1;
        int rightSize = right - mid;

        Node<K, V>[] leftArray = new Node[leftSize];
        Node<K, V>[] rightArray = new Node[rightSize];

        // Copy data to temp arrays
        System.arraycopy(array, left, leftArray, 0, leftSize);
        System.arraycopy(array, mid + 1, rightArray, 0, rightSize);

        int i = 0, j = 0, k = left;

        // Merge back by comparing keys
        while (i < leftSize && j < rightSize) {
            if (keyComparator.compare(leftArray[i].key, rightArray[j].key) <= 0) {
                array[k] = leftArray[i];
                i++;
            } else {
                array[k] = rightArray[j];
                j++;
            }
            k++;
        }
        
        // Copy remaining elements
        while (i < leftSize) {
            array[k] = leftArray[i];
            i++;
            k++;
        }

        while (j < rightSize) {
            array[k] = rightArray[j];
            j++;
            k++;
        }
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