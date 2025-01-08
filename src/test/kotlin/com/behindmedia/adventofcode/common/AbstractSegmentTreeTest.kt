package com.behindmedia.adventofcode.common

abstract class AbstractSegmentTreeTest {

    protected enum class Operation {
        Assign, Add
    }

    @JvmInline
    protected value class SumNode(val value: Int) : SegmentNode<SumNode, Int, Operation> {
        override fun plus(other: SumNode) = SumNode(this.value + other.value)
        override fun applyOperation(operation: Operation, value: Int, range: IntRange): SumNode {
            return when (operation) {
                Operation.Assign -> SumNode(value * range.size)
                Operation.Add -> SumNode(this.value + value * range.size)
            }
        }
        override fun toString(): String {
            return value.toString()
        }
    }

    protected data class LazySumNode(var value: Int = 0, var operation: Operation = Operation.Add) : LazySegmentNode<LazySumNode, SumNode, Int, Operation> {

        override fun applyFrom(parent: LazySumNode, node: SumNode) = applyOperation(parent.operation, parent.value, node)

        override fun applyOperation(operation: Operation, value: Int, node: SumNode) = when (operation) {
            Operation.Assign -> {
                this.operation = Operation.Assign
                this.value = value
            }
            Operation.Add -> this.value += value
        }

        override fun applyTo(node: SumNode, range: IntRange) = node.applyOperation(operation, value, range)

        override fun reset() {
            value = 0
            operation = Operation.Add
        }

        override val isPending: Boolean
            get() = value != 0 || operation != Operation.Add

        override fun toString(): String {
            return "${operation.name} $value"
        }
    }

    protected fun constructTree(arr: IntArray): LazySegmentTree<LazySumNode, SumNode, Int, Operation> {
        return LazySegmentTree(
            array = arr.toTypedArray(),
            nodeConstructor = ::SumNode,
            lazyNodeConstructor = { LazySumNode() })
    }
}