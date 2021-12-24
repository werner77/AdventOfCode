package com.behindmedia.adventofcode.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class implements a Fibonacci heap data structure. Much of the code in this class is based on
 * the algorithms in the "Introduction to Algorithms" by Cormen, Leiserson, and Rivest in Chapter
 * 21. The amortized running time of most of these methods is $O(1)$, making it a very fast data
 * structure. Several have an actual running time of $O(1)$. removeMin() and delete() have $O(log
 * n)$ amortized running times because they do the heap consolidation. If you attempt to store nodes
 * in this heap with key values of -Infinity (int.NEGATIVE_INFINITY) the <code>delete()</code>
 * operation may fail to remove the correct element.
 *
 * <p>
 * <b>Note that this implementation is not synchronized.</b> If multiple threads access a set
 * concurrently, and at least one of the threads modifies the set, it <i>must</i> be synchronized
 * externally. This is typically accomplished by synchronizing on some object that naturally
 * encapsulates the set.
 * </p>
 *
 * <p>
 * This class was originally developed by Nathan Fiedler for the GraphMaker project. It was imported
 * to JGraphT with permission, courtesy of Nathan Fiedler.
 * </p>
 *
 * @param <T> node data type
 *
 * @author Nathan Fiedler
 */
public class FibonacciHeap<T> {

    /**
     * Implements a node of the Fibonacci heap. It holds the information necessary for maintaining the
     * structure of the heap. It also holds the reference to the key value (which is used to determine
     * the heap structure).
     *
     * @param <T> node data type
     *
     * @author Nathan Fiedler
     */
    private static class FibonacciHeapNode<T>
    {
        /**
         * Node data.
         */
        T data;

        /**
         * first child node
         */
        FibonacciHeapNode<T> child;

        /**
         * left sibling node
         */
        FibonacciHeapNode<T> left;

        /**
         * parent node
         */
        FibonacciHeapNode<T> parent;

        /**
         * right sibling node
         */
        FibonacciHeapNode<T> right;

        /**
         * true if this node has had a child removed since this node was added to its parent
         */
        boolean mark;

        /**
         * key value for this node
         */
        int key;

        /**
         * number of children of this node (does not count grandchildren)
         */
        int degree;

        /**
         * Constructs a new node.
         *
         * @param data data for this node
         */
        public FibonacciHeapNode(T data)
        {
            this.data = data;
        }

        /**
         * Return the string representation of this object.
         *
         * @return string representing this object
         */
        @Override
        public String toString()
        {
            return Integer.toString(key);
        }

        // toString
    }

    private static final double ONE_OVER_LOG_PHI = 1.0 / Math.log((1.0 + Math.sqrt(5.0)) / 2.0);

    /**
     * Points to the minimum node in the heap.
     */
    private FibonacciHeapNode<T> minNode;

    /**
     * Number of nodes in the heap.
     */
    private int nNodes;

    /**
     * Constructs a FibonacciHeap object that contains no elements.
     */
    public FibonacciHeap()
    {
    } // FibonacciHeap

    /**
     * Tests if the Fibonacci heap is empty or not. Returns true if the heap is empty, false
     * otherwise.
     *
     * <p>
     * Running time: $O(1)$ actual
     * </p>
     *
     * @return true if the heap is empty, false otherwise
     */
    public boolean isEmpty()
    {
        return minNode == null;
    }

    // isEmpty

    /**
     * Removes all elements from this heap.
     */
    public void clear()
    {
        minNode = null;
        nNodes = 0;
        existingNodes.clear();
    }

    private final Map<T, FibonacciHeapNode<T>> existingNodes = new HashMap<>();

    @Nullable
    public Path<T> poll() {
        FibonacciHeapNode<T> minNode = removeMin();
        if (minNode != null) {
            existingNodes.remove(minNode.data);
            return new Path<T>(minNode.data, minNode.key, null);
        }
        return null;
    }

    /**
     * Inserts the node if it does not exists, else updates its priority using the supplied key.
     * If the existing key is already < than the supplied key this operation does nothing.
     */
    public void update(@NotNull Path<T> path) {
        update(path.getDestination(), (int) path.getPathLength());
    }

    public void update(@NotNull T node, int pathLength) {
        FibonacciHeapNode<T> existingNode = existingNodes.get(node);
        if (existingNode == null) {
            FibonacciHeapNode<T> newNode = new FibonacciHeapNode<>(node);
            insert(newNode, pathLength);
            existingNodes.put(node, newNode);
        } else if (pathLength < existingNode.key) {
            decreaseKey(existingNode, pathLength);
        }
    }

    @Nullable
    public Path<T> first() {
        if (minNode != null) {
            return new Path<T>(minNode.data, minNode.key, null);
        }
        return null;
    }

    // clear

    /**
     * Decreases the key value for a heap node, given the new value to take on. The structure of the
     * heap may be changed and will not be consolidated.
     *
     * <p>
     * Running time: $O(1)$ amortized
     * </p>
     *
     * @param x node to decrease the key of
     * @param k new key value for node x
     *
     * @exception IllegalArgumentException Thrown if $k$ is larger than x.key value.
     */
    private void decreaseKey(FibonacciHeapNode<T> x, int k)
    {
        if (k > x.key) {
            throw new IllegalArgumentException(
                    "decreaseKey() got larger key value. Current key: " + x.key + " new key: " + k);
        }

        if (x.right == null) {
            throw new IllegalArgumentException("Invalid heap node");
        }

        x.key = k;

        FibonacciHeapNode<T> y = x.parent;

        if ((y != null) && (x.key < y.key)) {
            cut(x, y);
            cascadingCut(y);
        }

        if (x.key < minNode.key) {
            minNode = x;
        }
    }

    // decreaseKey

    /**
     * Inserts a new data element into the heap. No heap consolidation is performed at this time,
     * the new node is simply inserted into the root list of this heap.
     *
     * <p>
     * Running time: $O(1)$ actual
     * </p>
     *
     * @param node new node to insert into heap
     * @param key key value associated with data object
     * @throws IllegalArgumentException if the node already belongs to a heap
     */
    private void insert(FibonacciHeapNode<T> node, int key)
    {
        if (node.right != null) {
            throw new IllegalArgumentException("Invalid heap node");
        }

        node.key = key;

        // concatenate node into min list
        if (minNode != null) {
            node.left = minNode;
            node.right = minNode.right;
            minNode.right = node;
            node.right.left = node;

            if (key < minNode.key) {
                minNode = node;
            }
        } else {
            node.left = node;
            node.right = node;
            minNode = node;
        }

        nNodes++;
    }

    // insert

    /**
     * Removes the smallest element from the heap. This will cause the trees in the heap to be
     * consolidated, if necessary.
     *
     * <p>
     * Running time: $O(\log n)$ amortized
     * </p>
     *
     * @return node with the smallest key
     */
    private FibonacciHeapNode<T> removeMin()
    {
        FibonacciHeapNode<T> z = minNode;

        if (z != null) {
            int numKids = z.degree;
            FibonacciHeapNode<T> x = z.child;
            FibonacciHeapNode<T> tempRight;

            // for each child of z do...
            while (numKids > 0) {
                tempRight = x.right;

                // remove x from child list
                x.left.right = x.right;
                x.right.left = x.left;

                // add x to root list of heap
                x.left = minNode;
                x.right = minNode.right;
                minNode.right = x;
                x.right.left = x;

                // set parent[x] to null
                x.parent = null;
                x = tempRight;
                numKids--;
            }

            // remove z from root list of heap
            z.left.right = z.right;
            z.right.left = z.left;

            if (z == z.right) {
                minNode = null;
            } else {
                minNode = z.right;
                consolidate();
            }

            // decrement size of heap
            nNodes--;

            // clear z
            z.left = null;
            z.right = null;
            z.degree = 0;
            z.child = null;
            z.mark = false;
        }

        return z;
    }

    // removeMin

    /**
     * Returns the size of the heap which is measured in the number of elements contained in the
     * heap.
     *
     * <p>
     * Running time: $O(1)$ actual
     * </p>
     *
     * @return number of elements in the heap
     */
    public int size()
    {
        return nNodes;
    }

    // size

    /**
     * Creates a String representation of this Fibonacci heap.
     *
     * @return String of this.
     */
    @Override
    public String toString()
    {
        if (minNode == null) {
            return "FibonacciHeap=[]";
        }

        // create a new stack and put root on it
        Deque<FibonacciHeapNode<T>> stack = new ArrayDeque<>();
        stack.push(minNode);

        StringBuilder buf = new StringBuilder(512);
        buf.append("FibonacciHeap=[");

        // do a simple breadth-first traversal on the tree
        while (!stack.isEmpty()) {
            FibonacciHeapNode<T> curr = stack.pop();
            buf.append(curr);
            buf.append(", ");

            if (curr.child != null) {
                stack.push(curr.child);
            }

            FibonacciHeapNode<T> start = curr;
            curr = curr.right;

            while (curr != start) {
                buf.append(curr);
                buf.append(", ");

                if (curr.child != null) {
                    stack.push(curr.child);
                }

                curr = curr.right;
            }
        }

        buf.append(']');

        return buf.toString();
    }

    // toString

    /**
     * Performs a cascading cut operation. This cuts y from its parent and then does the same for
     * its parent, and so on up the tree.
     *
     * <p>
     * Running time: $O(\log n)$; $O(1)$ excluding the recursion
     * </p>
     *
     * @param y node to perform cascading cut on
     */
    private void cascadingCut(@NotNull FibonacciHeapNode<T> y)
    {
        FibonacciHeapNode<T> z = y.parent;

        // if there's a parent of y...
        while (z != null) {
            // if y is marked, set it marked and finish
            if (!y.mark) {
                y.mark = true;
                return;
            } else {
                // y is marked, cut it from parent and continue cascading cut with z
                cut(y, z);
                y = z;
                z = z.parent;
            }
        }
    }

    // cascadingCut

    /**
     * Consolidate trees
     */
    private void consolidate()
    {
        int arraySize = ((int) Math.floor(Math.log(nNodes) * ONE_OVER_LOG_PHI)) + 1;

        List<FibonacciHeapNode<T>> array = new ArrayList<>(arraySize);

        // Initialize degree array
        for (int i = 0; i < arraySize; i++) {
            array.add(null);
        }

        // Find the number of root nodes.
        int numRoots = 0;
        FibonacciHeapNode<T> x = minNode;

        if (x != null) {
            numRoots++;
            x = x.right;

            while (x != minNode) {
                numRoots++;
                x = x.right;
            }
        }

        // For each node in root list do...
        while (numRoots > 0) {
            // Access this node's degree..
            int d = x.degree;
            FibonacciHeapNode<T> next = x.right;

            // ..and see if there's another of the same degree.
            for (;;) {
                FibonacciHeapNode<T> y = array.get(d);
                if (y == null) {
                    // Nope.
                    break;
                }

                // There is, make one of the nodes a child of the other.
                // Do this based on the key value.
                if (x.key > y.key) {
                    FibonacciHeapNode<T> temp = y;
                    y = x;
                    x = temp;
                }

                // FibonacciHeapNode<T> y disappears from root list.
                link(y, x);

                // We've handled this degree, go to next one.
                array.set(d, null);
                d++;
            }

            // Save this node for later when we might encounter another
            // of the same degree.
            array.set(d, x);

            // Move forward through list.
            x = next;
            numRoots--;
        }

        // Set min to null (effectively losing the root list) and
        // reconstruct the root list from the array entries in array[].
        minNode = null;

        for (int i = 0; i < arraySize; i++) {
            FibonacciHeapNode<T> y = array.get(i);
            if (y == null) {
                continue;
            }

            // We've got a live one, add it to root list.
            if (minNode != null) {
                // First remove node from root list.
                y.left.right = y.right;
                y.right.left = y.left;

                // Now add to root list, again.
                y.left = minNode;
                y.right = minNode.right;
                minNode.right = y;
                y.right.left = y;

                // Check if this is a new min.
                if (y.key < minNode.key) {
                    minNode = y;
                }
            } else {
                minNode = y;
            }
        }
    }

    // consolidate

    /**
     * The reverse of the link operation: removes $x$ from the child list of $y$. This method
     * assumes that min is non-null.
     *
     * <p>
     * Running time: $O(1)$
     * </p>
     *
     * @param x child of $y$ to be removed from $y$'s child list
     * @param y parent of $x$ about to lose a child
     */
    private void cut(FibonacciHeapNode<T> x, FibonacciHeapNode<T> y)
    {
        // remove x from childlist of y and decrement degree[y]
        x.left.right = x.right;
        x.right.left = x.left;
        y.degree--;

        // reset y.child if necessary
        if (y.child == x) {
            y.child = x.right;
        }

        if (y.degree == 0) {
            y.child = null;
        }

        // add x to root list of heap
        x.left = minNode;
        x.right = minNode.right;
        minNode.right = x;
        x.right.left = x;

        // set parent[x] to nil
        x.parent = null;

        // set mark[x] to false
        x.mark = false;
    }

    // cut

    /**
     * Make node $y$ a child of node $x$.
     *
     * <p>
     * Running time: $O(1)$ actual
     * </p>
     *
     * @param y node to become child
     * @param x node to become parent
     */
    private void link(FibonacciHeapNode<T> y, FibonacciHeapNode<T> x)
    {
        // remove y from root list of heap
        y.left.right = y.right;
        y.right.left = y.left;

        // make y a child of x
        y.parent = x;

        if (x.child == null) {
            x.child = y;
            y.right = y;
            y.left = y;
        } else {
            y.left = x.child;
            y.right = x.child.right;
            x.child.right = y;
            y.right.left = y;
        }

        // increase degree[x]
        x.degree++;

        // set mark[y] false
        y.mark = false;
    }

    // link
}

// FibonacciHeap
