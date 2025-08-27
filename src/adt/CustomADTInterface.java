package adt;

import org.w3c.dom.Node;

import java.util.Comparator;

/**
 * Interface for a versatile Abstract Data Type that combines the functionality of:
 * - Map (HashMap-like key-value storage with O(1) average lookup)
 * - List (Ordered collection with indexed access)
 * - Queue (FIFO operations)
 * - Stack (LIFO operations)
 * 
 * This interface defines the contract for a generic data structure suitable
 * for use across all modules in the clinic management system.
 *
 * @param <K> the type of keys maintained by this ADT
 * @param <V> the type of mapped values
 */
public interface CustomADTInterface<K, V> {

    // ===============================
    // MAP OPERATIONS
    // ===============================

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value is replaced.
     * 
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no mapping for key
     * @throws IllegalArgumentException if key is null
     */
    V put(K key, V value);

    /**
     * Returns the value to which the specified key is mapped,
     * or null if this map contains no mapping for the key.
     * 
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if no mapping exists
     */
    V get(K key);

    /**
     * Returns true if this map contains a mapping for the specified key.
     * 
     * @param key key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    boolean containsKey(K key);

    /**
     * Removes the mapping for the specified key from this map if present.
     * 
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    V remove(K key);

    // ===============================
    // LIST OPERATIONS
    // ===============================

    /**
     * Returns the element at the specified position in this list.
     * 
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    V get(int index);

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * 
     * @param index index of the element to replace
     * @param value element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    V set(int index, V value);

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent elements to the right.
     * 
     * @param index index at which the specified element is to be inserted
     * @param key the key for the element
     * @param value element to be inserted
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     * @throws IllegalArgumentException if the key already exists in the map
     */
    void add(int index, K key, V value);

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * 
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    V removeAt(int index);

    // ===============================
    // QUEUE OPERATIONS (FIFO)
    // ===============================

    /**
     * Inserts the specified element into this queue if it is possible to do so immediately
     * without violating capacity restrictions. This is a map-aware queue operation.
     * 
     * @param key the key for the element
     * @param value the element to add
     * @return true if the element was added to this queue, else false
     * @throws IllegalArgumentException if the key already exists
     */
    boolean offer(K key, V value);

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * This is a map-aware queue operation that removes from both the queue and the map.
     * 
     * @return the head of this queue, or null if this queue is empty
     */
    V poll();

    /**
     * Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
     * 
     * @return the head of this queue, or null if this queue is empty
     */
    V peek();

    // ===============================
    // STACK OPERATIONS (LIFO)
    // ===============================

    /**
     * Pushes an element onto the top of this stack.
     * This is a map-aware stack operation.
     * 
     * @param key the key for the element
     * @param value the element to be pushed onto this stack
     * @throws IllegalArgumentException if the key already exists
     */
    void push(K key, V value);

    /**
     * Removes the object at the top of this stack and returns that object as the value of this function.
     * This is a map-aware stack operation that removes from both the stack and the map.
     * 
     * @return the object at the top of this stack
     * @throws RuntimeException if this stack is empty
     */
    V pop();

    /**
     * Looks at the object at the top of this stack without removing it from the stack.
     * 
     * @return the object at the top of this stack
     * @throws RuntimeException if this stack is empty
     */
    V top();

    // ===============================
    // COLLECTION OPERATIONS
    // ===============================

    /**
     * Returns the number of key-value mappings in this map.
     * 
     * @return the number of key-value mappings in this map
     */
    int size();

    /**
     * Returns true if this map contains no key-value mappings.
     * 
     * @return true if this map contains no key-value mappings
     */
    boolean isEmpty();

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    void clear();

    /**
     * Returns an array containing all of the values in this collection in insertion order.
     * 
     * @return an array containing all of the values in this collection
     */
//    V[] toArray();
    V[] toArray(V[] array);

    // ===============================
    // UTILITY OPERATIONS
    // ===============================

    /**
     * Returns a string representation of this collection.
     * The string representation consists of a list of the collection's values
     * in insertion order, enclosed in square brackets ("[]").
     * Adjacent values are separated by the characters ", " (comma and space).
     * 
     * @return a string representation of this collection
     */
    @Override
    String toString();

    /**
     * Compares the specified object with this map for equality.
     * Returns true if the given object is also a map and the two maps
     * represent the same mappings.
     * 
     * @param obj object to be compared for equality with this map
     * @return true if the specified object is equal to this map
     */
    @Override
    boolean equals(Object obj);

    /**
     * Returns the hash code value for this map.
     * 
     * @return the hash code value for this map
     */
    @Override
    int hashCode();


    /**
     * Sorts the elements in this collection according to the order induced by the specified comparator.
     *
     * @param comparator the comparator to determine the order of the collection. A null value indicates that the elements' natural ordering should be used.
     */
    void sort(Comparator<V> comparator);

    /**
     * Check if the collection is sorted according to the given comparator
     * @param comparator the comparator to check ordering
     * @return true if sorted, false otherwise
     */
    boolean isSorted(Comparator<V> comparator);

    /**
     * Filters the elements in this collection based on the specified condition and reference value.
     *
     * @param referenceValue the value to compare each element against
     * @param condition the comparator that defines the filtering condition
     * @return a new CustomADT containing only the elements that satisfy the filtering condition
     */
    CustomADT<K, V> filter(V referenceValue, Comparator<V> condition);

    /**
     * Performs a range search on the elements in this collection based on the specified minimum and maximum values.
     *
     * @param min the minimum value of the range (inclusive)
     * @param max the maximum value of the range (inclusive)
     * @param comparator the comparator that defines the ordering of the elements
     * @return a new CustomADT containing only the elements that fall within the specified range
     */
    CustomADT<K, V> rangeSearch(V min, V max, Comparator<V> comparator);
}
