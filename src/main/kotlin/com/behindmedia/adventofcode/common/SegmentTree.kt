package com.behindmedia.adventofcode.common

interface SegmentNode<N : SegmentNode<N, V>, V : Any> {
    operator fun plus(other: N): N
}

class SegmentTree<N : SegmentNode<N, V>, V : Any>(
    val size: Int,
    private val nodeConstructor: (V) -> N,
    dataLocator: (Int) -> V
) {
    private val nodes: MutableList<N?>

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

    private fun query(nodeIndex: Int, left: Int, right: Int, queryLeft: Int, queryRight: Int): N? {
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

    private fun update(nodeIndex: Int, left: Int, right: Int, valueIndex: Int, value: V) {
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

    private operator fun N?.plus(other: N?): N? {
        return if (this == null) {
            other
        } else if (other == null) {
            this
        } else {
            this + other
        }
    }
}

class LazySegmentTree<N : SegmentNode<N, V>, V : Any>() {
    // TODO
}