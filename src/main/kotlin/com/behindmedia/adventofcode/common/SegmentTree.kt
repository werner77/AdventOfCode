package com.behindmedia.adventofcode.common

interface SegmentNode<N : SegmentNode<N, V, O>, V : Any, O: Any> {
    // This operation denotes the aggregation the segment tree does
    operator fun plus(other: N): N

    // This generates a new node by applying the specified value with the specified operation
    fun applyOperation(operation: O, value: V, range: IntRange): N
}

interface LazySegmentNode<L : LazySegmentNode<L, N, V, O>, N : SegmentNode<N, V, O>, V : Any, O: Any> {
    // This generates a new lazy update based on this one and the specified parent.
    // The node parameter is the data node representing this value in the tree.
    fun applyFrom(parent: L, node: N)

    // Applies this update to a node by generating a new node (clearing this update once it's done).
    fun applyTo(node: N, range: IntRange): N

    // Applies an operation to this node
    fun applyOperation(operation: O, value: V, node: N)

    fun reset()

    val isPending: Boolean
}

interface SegmentTree<N : SegmentNode<N, V, O>, V : Any, O: Any> {
    val size: Int
    val range: IntRange
        get() = 0 until size

    fun query(range: IntRange = this.range): N

    fun query(index: Int): N {
        require(index in this.range) { "Invalid index: $index" }
        return query(index..index)
    }

    fun update(range: IntRange = this.range, value: V, operation: O) {
        for (i in range) {
            update(i, value, operation)
        }
    }

    fun update(index: Int, value: V, operation: O)
}

abstract class AbstractSegmentTree<N : SegmentNode<N, V, O>, V : Any, O: Any>(
    override val size: Int,
    protected val nodeConstructor: (V) -> N,
    dataLocator: (Int) -> V
): SegmentTree<N, V, O> {
    protected val nodes: MutableList<N?>

    init {
        require(size >= 1)
        val infoSize = 2 * (1 shl ceilLog2(size))
        nodes = MutableList<N?>(infoSize) {
            null
        }
        fun build(nodeIndex: Int, left: Int, right: Int) {
            if (left == right) {
                nodes[nodeIndex] = nodeConstructor(dataLocator(left))
                return
            }
            val mid = (left + right) / 2
            val nextNodeIndex = nodeIndex + nodeIndex
            build(nextNodeIndex, left, mid)
            build(nextNodeIndex + 1, mid + 1, right)
            nodes[nodeIndex] = nodes[nextNodeIndex] + nodes[nextNodeIndex + 1]
        }
        build(1, 0, size - 1)
    }

    override fun query(range: IntRange): N {
        require(range.first in this.range && range.last in this.range) { "Invalid range: $range" }
        return query(1, 0, size - 1, range.first, range.last) ?: error("No node found")
    }

    override fun update(index: Int, value: V, operation: O) {
        require(index in this.range) { "Invalid index: $index" }
        update(1, 0, size - 1, index, value, operation)
    }

    protected abstract fun query(nodeIndex: Int, left: Int, right: Int, queryLeft: Int, queryRight: Int): N?

    protected abstract fun update(nodeIndex: Int, left: Int, right: Int, valueIndex: Int, value: V, operation: O)

    protected fun requireNode(nodeIndex: Int): N {
        return requireNotNull(nodes[nodeIndex]) { "Node at index $nodeIndex does not exist" }
    }

    protected operator fun N?.plus(other: N?): N? {
        return if (this == null) {
            other
        } else if (other == null) {
            this
        } else {
            this + other
        }
    }
}

class PlainSegmentTree<N : SegmentNode<N, V, O>, V : Any, O: Any>(
    size: Int,
    nodeConstructor: (V) -> N,
    dataLocator: (Int) -> V
) : AbstractSegmentTree<N, V, O>(size, nodeConstructor, dataLocator) {
    constructor(array: Array<V>, nodeConstructor: (V) -> N, ) : this(
        array.size,
        nodeConstructor,
        { array[it] })

    constructor(list: List<V>, nodeConstructor: (V) -> N) : this(
        list.size,
        nodeConstructor,
        { list[it] })

    override fun query(nodeIndex: Int, left: Int, right: Int, queryLeft: Int, queryRight: Int): N? {
        if (right < queryLeft || left > queryRight) {
            return null
        }
        if (left >= queryLeft && right <= queryRight) {
            return nodes[nodeIndex]
        }
        val mid = (left + right) / 2
        val nextNodeIndex = nodeIndex shl 1
        return query(nextNodeIndex, left, mid, queryLeft, queryRight) + query(
            nextNodeIndex + 1,
            mid + 1,
            right,
            queryLeft,
            queryRight
        )
    }

    override fun update(nodeIndex: Int, left: Int, right: Int, valueIndex: Int, value: V, operation: O) {
        if (left == right) {
            val existing = nodes[nodeIndex] ?: error("Node at index $nodeIndex does not exist")
            nodes[nodeIndex] = existing.applyOperation(operation, value, left..right)
            return
        }
        val mid = (left + right) / 2
        val nextNodeIndex = nodeIndex shl 1
        if (valueIndex <= mid) {
            update(nextNodeIndex, left, mid, valueIndex, value, operation)
        } else {
            update(nextNodeIndex + 1, mid + 1, right, valueIndex, value, operation)
        }
        nodes[nodeIndex] = nodes[nextNodeIndex] + nodes[nextNodeIndex + 1]
    }
}

class LazySegmentTree<L : LazySegmentNode<L, N, V, O>, N : SegmentNode<N, V, O>, V : Any, O: Any>(
    size: Int,
    nodeConstructor: (V) -> N,
    lazyNodeConstructor: () -> L,
    dataLocator: (Int) -> V
) : AbstractSegmentTree<N, V, O>(size, nodeConstructor, dataLocator) {

    constructor(array: Array<V>, nodeConstructor: (V) -> N, lazyNodeConstructor: () -> L) : this(
        array.size,
        nodeConstructor,
        lazyNodeConstructor,
        { array[it] })

    constructor(list: List<V>, nodeConstructor: (V) -> N, lazyNodeConstructor: () -> L) : this(
        list.size,
        nodeConstructor,
        lazyNodeConstructor,
        { list[it] })

    private val lazyNodes: MutableList<L> = MutableList<L>(nodes.size) { lazyNodeConstructor.invoke() }

    override fun update(range: IntRange, value: V, operation: O) {
        require(range in this.range) { "Invalid range: $range" }
        update(1, 0, size - 1, range.first, range.last, value, operation)
    }

    override fun update(nodeIndex: Int, left: Int, right: Int, valueIndex: Int, value: V, operation: O) {
        update(nodeIndex, left, right, valueIndex, valueIndex, value, operation)
    }

    override fun query(nodeIndex: Int, left: Int, right: Int, queryLeft: Int, queryRight: Int): N? {
        // 1) First, apply any pending updates
        applyLazy(nodeIndex, left, right)

        // 2) Check for overlap
        if (right < queryLeft || left > queryRight) {
            return null
        }

        if (left >= queryLeft && right <= queryRight) {
            return nodes[nodeIndex]
        }

        // 3) Partial overlap
        val mid = (left + right) / 2
        val leftChildIndex = nodeIndex shl 1
        val rightChildIndex = leftChildIndex + 1
        return query(leftChildIndex, left, mid, queryLeft, queryRight) + query(
            rightChildIndex,
            mid + 1,
            right,
            queryLeft,
            queryRight
        )
    }

    private fun update(nodeIndex: Int, left: Int, right: Int, updateLeft: Int, updateRight: Int, value: V, operation: O) {

        // 2) If there's a pending update at this node, apply it
        applyLazy(nodeIndex, left, right)

        if (right < updateLeft || left > updateRight) {
            return
        }

        val leftChildIndex = nodeIndex shl 1
        val rightChildIndex = leftChildIndex + 1

        // 3) Total overlap: store 'value' in lazy[nodeIndex] and apply immediately
        if (updateLeft <= left && right <= updateRight) {

            // Apply the change to this node
            nodes[nodeIndex] = requireNode(nodeIndex).applyOperation(operation, value, left..right)

            if (left != right) {
                // Apply lazy to children
                lazyNodes[leftChildIndex].applyOperation(operation, value, requireNode(leftChildIndex))
                lazyNodes[rightChildIndex].applyOperation(operation, value, requireNode(rightChildIndex))
            }

            return
        }

        // 4) Partial overlap: recurse to children
        val mid = (left + right) / 2
        update(leftChildIndex, left, mid, updateLeft, updateRight, value, operation)
        update(rightChildIndex, mid + 1, right, updateLeft, updateRight, value, operation)

        // 5) Recombine children after updates
        nodes[nodeIndex] = nodes[leftChildIndex] + nodes[rightChildIndex]
    }

    private fun applyLazy(nodeIndex: Int, left: Int, right: Int) {
        val lazyUpdate = lazyNodes[nodeIndex]
        if (!lazyUpdate.isPending) {
            return
        }

        // 1) Apply the pending update to the current node
        val node = nodes[nodeIndex] ?: error("Node at index $nodeIndex does not exist")
        nodes[nodeIndex] = lazyUpdate.applyTo(node, left..right)

        // 2) Propagate to children if not a leaf
        if (left != right) {
            val leftChildIndex = nodeIndex shl 1
            val rightChildIndex = leftChildIndex + 1
            lazyNodes[leftChildIndex].applyFrom(lazyUpdate, requireNode(leftChildIndex))
            lazyNodes[rightChildIndex].applyFrom(lazyUpdate, requireNode(rightChildIndex))
        }

        // 3) Clear the lazy value for the current node
        lazyUpdate.reset()
    }
}

fun <N : SegmentNode<N, V, Unit>, V : Any> SegmentTree<N, V, Unit>.update(index: Int, value: V) {
    return this.update(index, value, Unit)
}

fun <N : SegmentNode<N, V, Unit>, V : Any> SegmentTree<N, V, Unit>.update(range: IntRange, value: V) {
    return this.update(range, value, Unit)
}
