package com.behindmedia.adventofcode.common

interface SegmentNode<N : SegmentNode<N, V>, V : Any> {
    operator fun plus(other: N): N
}

interface LazySegmentNode<L : LazySegmentNode<L, N, V>, N : SegmentNode<N, V>, V : Any> {
    operator fun plus(other: L): L
    fun applyTo(node: N, range: IntRange): N
}

abstract class AbstractSegmentTree<N : SegmentNode<N, V>, V : Any>(
    val size: Int,
    protected val nodeConstructor: (V) -> N,
    dataLocator: (Int) -> V
) {
    protected val nodes: MutableList<N?>

    val range: IntRange
        get() = 0 until size

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

    fun query(range: IntRange = this.range): N {
        require(range.first in this.range && range.last in this.range) { "Invalid range: $range" }
        return query(1, 0, size - 1, range.first, range.last) ?: error("No node found")
    }

    fun query(index: Int): N {
        require(index in this.range) { "Invalid index: $index" }
        return query(index..index)
    }

    fun update(index: Int, value: V) {
        require(index in this.range) { "Invalid index: $index" }
        update(1, 0, size - 1, index, value);
    }

    protected abstract fun query(nodeIndex: Int, left: Int, right: Int, queryLeft: Int, queryRight: Int): N?

    protected abstract fun update(nodeIndex: Int, left: Int, right: Int, valueIndex: Int, value: V)

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

class SegmentTree<N : SegmentNode<N, V>, V : Any>(
    size: Int,
    nodeConstructor: (V) -> N,
    dataLocator: (Int) -> V
) : AbstractSegmentTree<N, V>(size, nodeConstructor, dataLocator) {
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
        val nextNodeIndex = nodeIndex + nodeIndex
        return query(nextNodeIndex, left, mid, queryLeft, queryRight) + query(
            nextNodeIndex + 1,
            mid + 1,
            right,
            queryLeft,
            queryRight
        )
    }

    override fun update(nodeIndex: Int, left: Int, right: Int, valueIndex: Int, value: V) {
        if (left == right) {
            nodes[nodeIndex] = nodeConstructor(value)
            return
        }
        val mid = (left + right) / 2
        if (valueIndex <= mid) {
            update(nodeIndex + nodeIndex, left, mid, valueIndex, value)
        } else {
            update(nodeIndex + nodeIndex + 1, mid + 1, right, valueIndex, value)
        }
        nodes[nodeIndex] = nodes[nodeIndex + nodeIndex] + nodes[nodeIndex + nodeIndex + 1]
    }
}

class LazySegmentTree<L : LazySegmentNode<L, N, V>, N : SegmentNode<N, V>, V : Any>(
    size: Int,
    nodeConstructor: (V) -> N,
    private val lazyNodeConstructor: (V) -> L,
    dataLocator: (Int) -> V
) : AbstractSegmentTree<N, V>(size, nodeConstructor, dataLocator) {

    constructor(array: Array<V>, nodeConstructor: (V) -> N, lazyNodeConstructor: (V) -> L) : this(
        array.size,
        nodeConstructor,
        lazyNodeConstructor,
        { array[it] })

    constructor(list: List<V>, nodeConstructor: (V) -> N, lazyNodeConstructor: (V) -> L) : this(
        list.size,
        nodeConstructor,
        lazyNodeConstructor,
        { list[it] })

    private val lazyNodes: MutableList<L?> = MutableList<L?>(nodes.size) { null }

    fun update(range: IntRange, value: V) {
        require(range in this.range) { "Invalid range: $range" }
        update(1, 0, size - 1, range.first, range.last, value)
    }

    override fun update(nodeIndex: Int, left: Int, right: Int, valueIndex: Int, value: V) {
        update(nodeIndex, left, right, valueIndex, valueIndex, value)
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

    private fun update(nodeIndex: Int, left: Int, right: Int, updateLeft: Int, updateRight: Int, value: V) {
        // 1) If there's a pending update at this node, apply it
        applyLazy(nodeIndex, left, right)

        // 2) No overlap
        if (right < updateLeft || left > updateRight) return

        // 3) Total overlap: store 'value' in lazy[nodeIndex] and apply immediately
        if (updateLeft <= left && right <= updateRight) {
            lazyNodes[nodeIndex] += lazyNodeConstructor(value)
            applyLazy(nodeIndex, left, right)
            return
        }

        // 4) Partial overlap: recurse to children
        val mid = (left + right) / 2
        val leftChildIndex = nodeIndex shl 1
        val rightChildIndex = leftChildIndex + 1
        update(leftChildIndex, left, mid, updateLeft, updateRight, value)
        update(rightChildIndex, mid + 1, right, updateLeft, updateRight, value)

        // 5) Recombine children after updates
        nodes[nodeIndex] = nodes[leftChildIndex] + nodes[rightChildIndex]
    }

    private fun applyLazy(nodeIndex: Int, left: Int, right: Int) {
        val pendingUpdate = lazyNodes[nodeIndex] ?: return

        // 1) Apply the pending update to the current node
        val node = nodes[nodeIndex] ?: error("Node at index $nodeIndex does not exist")
        nodes[nodeIndex] = pendingUpdate.applyTo(node, left..right)

        // 2) Propagate to children if not a leaf
        if (left != right) {
            val leftChild = nodeIndex shl 1
            val rightChild = leftChild + 1
            // If child has no lazy value, set it; if it does, combine them
            lazyNodes[leftChild] = lazyNodes[leftChild] + pendingUpdate
            lazyNodes[rightChild] = lazyNodes[rightChild] + pendingUpdate
        }

        // 3) Clear the lazy value for the current node
        lazyNodes[nodeIndex] = null
    }

    private operator fun L?.plus(other: L?): L? {
        return if (this == null) {
            other
        } else if (other == null) {
            this
        } else {
            this + other
        }
    }
}
