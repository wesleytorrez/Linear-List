package data_structures;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/**
 * Programming Assignment #2
 * The LinearList class is a doubly linked list
 * implementation that operates primarily in O(1)
 * time. The doubly linked list can traverse forward
 * and backward throughout the list, and it has the
 * ability to remove from the tail, unlike the singly
 * linked list. It also always has the proper size,
 * and adjusts itself if any elements are added or
 * removed to or from the list. Its iterator has fail
 * fast capabilities, and becomes tainted if there have
 * been any changes to the iterator. It has a stateCheck
 * and modificationCount variable to determine whether it
 * should fail fast or not. The modificationCounter
 * variable increments anytime an add/remove operation
 * is done on the list or when the list is cleared.
 * Inside the IteratorHelper class, if modificationCounter
 * is not equal to the stateCheck, then an exception
 * will be thrown and the program will fail fast, as
 * expected.
 * CS310
 * 3/11/2019
 * @author Wesley Torrez cssc1517
 */

@SuppressWarnings("unchecked")
public class LinearList<E extends Comparable<E>>
        implements LinearListADT<E> {
    private int currentSize;
    private long modificationCounter;

    private class Node<E> {
        E data;
        Node<E> previous, next; // these variables
                               // allow traversal
                              // throughout the list

        private Node(E data) {
            this.data = data;
            previous = null;
            next = null;
        }
    }

    public LinearList() {
        head = tail = null;
        currentSize = 0;
        modificationCounter = 0;
    }

    private Node<E> head, tail;

    @Override
    public boolean addFirst(E obj) {
        Node<E> newNode = new Node(obj);
        if (isEmpty()) {
            head = tail = newNode;
        }
        else  {
            head.previous = newNode;
            newNode.next = head;
            head = newNode;
        }
        currentSize++;
        modificationCounter++;
        return true;
    }

    @Override
    public boolean addLast(E obj) {
        Node<E> newNode = new Node(obj);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;
        }
        currentSize++;
        modificationCounter++;
        return true;
    }

    @Override
    public E removeFirst() {
        E firstElement = peekFirst();
        if (isEmpty()) {
            return null;
        } else  {
            if (currentSize == 1) {
                clear();
            } else {
                head.next.previous = null;
                head = head.next;
                currentSize--;
                modificationCounter++;
            }
            return firstElement;
        }
    }

    @Override
    public E removeLast() {
        E lastElement = peekLast();
        if (isEmpty()) {
            return null;
        } else {
            if (currentSize == 1) {
                clear();
            } else {
                tail.previous.next = null;
                tail = tail.previous;
                currentSize--;
                modificationCounter++;
            }
            return lastElement;
        }
    }

    /*
    * If the element to be removed is located at the head or tail,
    * the removal is done in O(1) time. Otherwise, the method must
    * traverse through the list and search for the element in order
    * to find and remove it. In this case, the operation time is O(n).
    * The traversal through the list is accomplished by using a
    * while loop that checks whether each node's data in the
    * list is equal to the element that needs to be removed.
    * Starting from the head, the loop moves down the list until
    * it has found the element it needs to remove or it has reached
    * the end of the list, which is when next is null.
    */
    @Override
    public E remove(E obj) {
        if (isEmpty())
            return null;
        else {
            Node<E> headPointer = head;
            Node<E> tailPointer = tail;
            if (obj.compareTo(peekFirst()) == 0) {
                removeFirst();
                modificationCounter++;
                return headPointer.data;
            } else if (obj.compareTo(peekLast()) == 0) {
                removeLast();
                modificationCounter++;
                return tailPointer.data;
            } else {
                while (true) {
                    if (headPointer.data.compareTo(obj) == 0) {
                        Node tempHead = headPointer.previous;
                        Node tempTail = headPointer.next;
                        tempHead.next = tempTail;
                        tempTail.previous = tempHead;
                        currentSize--;
                        modificationCounter++;
                        return headPointer.data;
                    } else if (headPointer.next == null) {
                        return null;
                    } else
                        headPointer = headPointer.next;
                }
            }
        }
    }

    @Override
    public E peekFirst() {
        if (isEmpty())
            return null;
        else
            return head.data;
    }

    @Override
    public E peekLast() {
        if (isEmpty())
            return null;
        else
            return tail.data;
    }

    /*
     * This method works the say way as the remove method. It
     * can operate in O(1) time in the best case and O(n) time
     * in the worst case when it has to search and traverse the list.
     */
    @Override
    public boolean contains(E obj) {
        Node<E> currentNode = head;
        if (isEmpty())
            return false;
        else if (obj.compareTo(peekFirst()) == 0)
            return true;
        else if (obj.compareTo(peekLast()) == 0)
            return true;
        else {
            while (true) {
                if (obj.compareTo(currentNode.data) == 0) {
                    return true;
                } else if (currentNode.next == null)
                    return false;
                else
                    currentNode = currentNode.next;
            }
        }
    }

    /*
     * This method works the say way as the remove method. It
     * can operate in O(1) time in the best case and O(n) time
     * in the worst case when it has to search and traverse the list.
     */
    @Override
    public E find(E obj) {
        if (isEmpty())
            return null;
        else {
            Node<E> currentNode = head;
            while (true) {
                if (currentNode.data.compareTo(obj) == 0) {
                    return currentNode.data;
                } else if (currentNode.next == null)
                    return null;
                else
                    currentNode = currentNode.next;
            }
        }
    }

    @Override
    public void clear() {
        head = tail = null;
        currentSize = 0;
        modificationCounter++;
    }

    @Override
    public boolean isEmpty() {
        return (currentSize == 0);
    }

    @Override
    public boolean isFull() { return false; }

    @Override
    public int size() { return currentSize; }

    @Override
    public Iterator<E> iterator() {
        return new IteratorHelper();
    }

    public class IteratorHelper implements Iterator<E> {
        Node<E> iterPointer;
        long stateCheck;

        private IteratorHelper() {
            iterPointer = head;
            stateCheck = modificationCounter;
        }

        @Override
        public boolean hasNext() {
                if (stateCheck != modificationCounter)
                    throw new ConcurrentModificationException();
            return iterPointer != null && currentSize != 0;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            else  {
                E element = iterPointer.data;
                iterPointer = iterPointer.next;
                return element;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}