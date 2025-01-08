package com.behindmedia.adventofcode.common

abstract class AbstractSegmentTreeTest {

    protected enum class Operation {
        Assign, Add
    }

    @JvmInline
    protected value class ValueNode(val value: Int) : SegmentNode<ValueNode, Int, Operation> {
        override fun plus(other: ValueNode) = ValueNode(this.value + other.value)
        override fun applyOperation(operation: Operation, value: Int, range: IntRange): ValueNode {
            return when (operation) {
                Operation.Assign -> ValueNode(value * range.size)
                Operation.Add -> ValueNode(this.value + value * range.size)
            }
        }
        override fun toString(): String {
            return value.toString()
        }
    }

    protected data class LazyNode(var value: Int = 0, var operation: Operation = Operation.Add) : LazySegmentNode<LazyNode, ValueNode, Int, Operation> {

        override fun applyFrom(
            parent: LazyNode,
            node: ValueNode
        ) = applyOperation(parent.operation, parent.value, node)

        override fun applyOperation(
            operation: Operation,
            value: Int,
            node: ValueNode
        ) = when (operation) {
            Operation.Assign -> {
                this.operation = Operation.Assign
                this.value = value
            }
            Operation.Add -> this.value += value
        }

        override fun applyTo(node: ValueNode, range: IntRange) = node.applyOperation(operation, value, range)

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

    protected fun constructTree(arr: IntArray): LazySegmentTree<LazyNode, ValueNode, Int, Operation> {
        return LazySegmentTree(
            array = arr.toTypedArray(),
            nodeConstructor = ::ValueNode,
            lazyNodeConstructor = { LazyNode() })
    }
}