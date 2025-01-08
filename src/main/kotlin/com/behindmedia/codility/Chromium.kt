package com.behindmedia.codility

import com.behindmedia.adventofcode.common.*

private typealias ModLong = BoundedLong

class Chromium {

    private data class Nest(val index: Int, val height: Int)

    enum class ProcessMode {
        Initial, Left, Right
    }

    private data class Node(val left: ModLong = ModLong.ZERO, val right: ModLong = ModLong.ZERO) : SegmentNode<Node, ProcessMode, Unit> {
        override fun plus(other: Node): Node = Node(left + other.left, right + other.right)

        override fun applyOperation(
            operation: Unit,
            value: ProcessMode,
            range: IntRange
        ) = when (value) {
            ProcessMode.Initial -> Node(ModLong.ONE, ModLong.ONE)
            ProcessMode.Left -> this.copy(left = this.left + this.right)
            ProcessMode.Right -> this.copy(right = this.left + this.right)
        }

        val isProcessed: Boolean = left != ModLong.ZERO || right != ModLong.ZERO
    }

    private data class LazyNode(
        private var ll: ModLong = ModLong.ZERO,
        private var rl: ModLong = ModLong.ZERO,
        private var lr: ModLong = ModLong.ZERO,
        private var rr: ModLong = ModLong.ZERO,
        private var left: ModLong = ModLong.ZERO,
        private var right: ModLong = ModLong.ZERO
    ) : LazySegmentNode<LazyNode, Node, ProcessMode, Unit> {

        companion object {
            val EMPTY = LazyNode()
        }

        override fun reset() {
            ll = ModLong.ZERO
            rl = ModLong.ZERO
            lr = ModLong.ZERO
            rr = ModLong.ZERO
            left = ModLong.ZERO
            right = ModLong.ZERO
        }

        override val isPending: Boolean
            get() = this != EMPTY

        override fun applyFrom(parent: LazyNode, node: Node) {
            if (!node.isProcessed) {
                return
            }
            val deltaRR = parent.rr + parent.rr * this.rr + parent.lr * this.rl
            val deltaLR = parent.lr + parent.lr * this.ll + this.lr * parent.rr
            val deltaLL = parent.ll + parent.ll * this.ll + parent.rl * this.lr
            val deltaRL = parent.rl + parent.rl * this.rr + this.rl * parent.ll
            ll = this.ll + deltaLL
            rl = this.rl + deltaRL
            lr = this.lr + deltaLR
            rr = this.rr + deltaRR
        }

        override fun applyOperation(
            operation: Unit,
            value: ProcessMode,
            node: Node
        ) {
            if (!node.isProcessed) {
                if (value == ProcessMode.Initial) {
                    left = ModLong.ONE
                    right = ModLong.ONE
                }
                return
            }
            when (value) {
                ProcessMode.Left -> {
                    this.rl += ModLong.ONE + this.rr
                    this.ll += this.lr
                }

                ProcessMode.Right -> {
                    this.lr += ModLong.ONE + this.ll
                    this.rr += this.rl
                }

                else -> {}
            }
        }

        override fun applyTo(
            node: Node,
            range: IntRange
        ): Node {
            val deltaLeft = this.left + this.rl * node.right + this.ll * node.left
            val deltaRight = this.right + this.rr * node.right + this.lr * node.left
            return Node(left = node.left + deltaLeft, right = node.right + deltaRight)
        }
    }

    private fun SegmentTree<Node, ProcessMode, Unit>.processElement(index: Int): ModLong {

        fun getSumLeft(): ModLong {
            return if (index > 0) this.query(0..index - 1).left else ModLong.ZERO
        }

        fun getSumRight(): ModLong {
            return if (index < size - 1) this.query(index + 1..size - 1).right else ModLong.ZERO
        }

        // The number of ways we finished at a lower nest while ending in the left direction
        val sumLeft = getSumLeft()

        // The number of ways we finished at a lower nest while ending in the right direction
        val sumRight = getSumRight()

        // Total number of ways to finish at this nest
        val total = (sumLeft + sumRight + ModLong.ONE)

        update(index, ProcessMode.Initial)

        // update current node/nest combinations
        if (index > 0) {
            // update nodes on the left
            update(0..index - 1, ProcessMode.Right)
        }
        if (index < size - 1) {
            // update nodes on the right
            update(index + 1..size - 1, ProcessMode.Left)
        }
        return total
    }

    fun solution(H: IntArray): Int {
        ModLong.MOD = 1_000_000_007L
        val nests = ArrayList<Nest>(H.size)
        for (i in 0 until H.size) {
            nests.add(Nest(i, H[i]))
        }
        nests.sortBy(Nest::height)
        val tree = LazySegmentTree<LazyNode, Node, ProcessMode, Unit>(
            size = H.size,
            nodeConstructor = { it -> Node() },
            lazyNodeConstructor = { LazyNode() },
            dataLocator = { it -> ProcessMode.Initial }
        )

        var total = ModLong.ZERO
        for (nest in nests) {
            total += tree.processElement(nest.index)
        }
        return total.value.toInt()
    }

    fun dpSolution(H: IntArray): Int {
        ModLong.MOD = 1_000_000_007L
        val nests: List<Nest> = H.withIndex().map { (i, h) -> Nest(i, h) }
            .sortedBy { it.height }
        var total = ModLong.ZERO
        for ((height, nest) in nests.withIndex()) {
            val index = nest.index
            var left = ModLong.ZERO
            var right = ModLong.ZERO
            for (k in height + 1 until nests.size) {
                val nest1 = nests[k]
                val isLeft = nest1.index < index
                if (isLeft) {
                    left += ModLong.ONE + right
                } else {
                    right += ModLong.ONE + left
                }
            }
            total += ModLong.ONE + left + right
        }
        return total.value.toInt()
    }

}

private fun generateRandomArray(size: Int): IntArray {
    return IntArray(size) { it }.also { it.shuffle() }
}

fun main() {
    val array = generateRandomArray(10_000)
    println(Chromium().solution(array))
    println(Chromium().dpSolution(array))
}