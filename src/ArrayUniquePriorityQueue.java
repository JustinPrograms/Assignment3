/**
 * This file contains the implementation of a unique priority queue using arrays.
 * It allows the addition of elements with priorities and ensures that no duplicate elements are added.
 * The priority queue maintains the elements in ascending order based on their priorities.
 *
 * @param <T> The type of elements stored in the priority queue.
 * @author Justin Dhillon JDHILL94 251348823
 */
public class ArrayUniquePriorityQueue<T> implements UniquePriorityQueueADT<T> {

    // Array to store the elements of the queue
    private T[] queue;
    // Array to store the priorities corresponding to the elements in the queue
    private double[] priority;
    // Variable to keep track of the number of elements in the queue
    private int count;

    /**
     * Constructs an empty priority queue with an initial capacity of 10.
     * Initializes the arrays for storing elements and priorities.
     */
    public ArrayUniquePriorityQueue() {
        count = 0;
        queue = (T[]) new Object[10]; // Initial capacity of 10
        priority = new double[10]; // Initial capacity of 10
    }

    /**
     * Adds a new element with the specified priority to the priority queue.
     * If the element already exists in the queue, it is not added again.
     *
     * @param data The element to be added to the priority queue.
     * @param prio The priority associated with the element.
     */
    public void add(T data, double prio) {
        // Check if the data item already exists in the queue
        for (int i = 0; i < count; i++) {
            if (queue[i].equals(data)) {
                return; // Data already exists, do nothing
            }
        }


        // Expand the capacity if the arrays are full
        if (count == queue.length) {
            expandCapacity();
        }

        // Find the correct index to insert the new item while maintaining proper ordering
        int index = count - 1;
        while (index >= 0 && priority[index] > prio) {
            index--;
        }

        // Move elements to make room for the new item
        for (int i = count - 1; i > index; i--) {
            queue[i + 1] = queue[i];
            priority[i + 1] = priority[i];
        }

        // Insert the new item at the correct index
        queue[index + 1] = data;
        priority[index + 1] = prio;

        // Increment the count
        count++;
    }

    /**
     * Expands the capacity of the priority queue by 5 elements.
     * Copies existing elements and priorities to the new arrays.
     */
    private void expandCapacity() {
        T[] newQueue = (T[]) new Object[queue.length + 5];
        double[] newPriority = new double[priority.length + 5];

        // Copy elements from the old arrays to the new arrays
        for (int i = 0; i < count; i++) {
            newQueue[i] = queue[i];
            newPriority[i] = priority[i];
        }

        // Update references to the new arrays
        queue = newQueue;
        priority = newPriority;
    }

    /**
     * Checks if the priority queue contains the specified element.
     *
     * @param data The element to be checked for existence in the priority queue.
     * @return true if the element is found in the priority queue, false otherwise.
     */
    public boolean contains(T data) {
        for (int i = 0; i < count; i++) {
            if (queue[i].equals(data)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves, but does not remove, the element with the minimum priority in the priority queue.
     *
     * @return The element with the minimum priority.
     * @throws CollectionException if the priority queue is empty.
     */
    public T peek() throws CollectionException {
        if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }

        double minPriority = priority[0]; // Initialize the minimum priority
        T minItem = queue[0]; // Initialize the corresponding item with the minimum priority

        // Iterate over the elements in the priority queue
        for (int i = 1; i < count; i++) {
            if (priority[i] < minPriority) {
                minPriority = priority[i]; // Update the minimum priority
                minItem = queue[i]; // Update the corresponding item
            }
        }

        return minItem; // Return the item with the smallest priority
    }


    public T removeMin() throws CollectionException {
        if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }

        // Find the index of the smallest priority
        int minIndex = 0;
        for (int i = 1; i < count; i++) {
            if (priority[i] < priority[minIndex]) {
                minIndex = i;
            }
        }

        // Get the item with the smallest priority
        T minItem = queue[minIndex];

        // Shift subsequent values over to the left
        for (int i = minIndex; i < count - 1; i++) {
            queue[i] = queue[i + 1];
            priority[i] = priority[i + 1];
        }

        // Decrement count
        count--;

        return minItem;
    }



    public void updatePriority(T data, double newPrio) throws CollectionException {
        int dataIndex = -1;

        // Find the index of the given data item in the queue
        for (int i = 0; i < count; i++) {
            if (queue[i].equals(data)) {
                dataIndex = i;
                break;
            }
        }

        // If the data item is not found, throw CollectionException
        if (dataIndex == -1) {
            throw new CollectionException("Item not found in PQ");
        }

        // Update the priority of the existing data item
        priority[dataIndex] = newPrio;

        // Remove the existing item and its priority from the arrays
        for (int i = dataIndex; i < count - 1; i++) {
            queue[i] = queue[i + 1];
            priority[i] = priority[i + 1];
        }

        // Decrement count
        count--;

        // Re-add the data item with its updated priority
        add(data, newPrio);


    }


    public boolean isEmpty() {
        return (size() == 0);
    }

    public int getLength() {
        return queue.length;
    }

    public int size() {
        return count;
    }

    public String toString() {
        if (isEmpty()) {
            return "The PQ is empty";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                sb.append(queue[i]).append(" [").append(priority[i]).append("], ");
            }
            // Remove the trailing comma and space
            sb.setLength(sb.length() - 2);
            return sb.toString();
        }
    }




}
