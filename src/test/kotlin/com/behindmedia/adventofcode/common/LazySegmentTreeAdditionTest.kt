package com.behindmedia.adventofcode.common

import kotlin.test.Test
import kotlin.test.assertEquals

class LazySegmentTreeAdditionTest {

    private enum class Operation {
        Assign, Add
    }

    @JvmInline
    private value class ValueNode(val value: Int) : SegmentNode<ValueNode, Int, Operation> {
        override fun plus(other: ValueNode) = ValueNode(this.value + other.value)
        override fun apply(value: Int, operation: Operation): ValueNode {
            return when (operation) {
                Operation.Assign -> ValueNode(value)
                Operation.Add -> ValueNode(this.value + value)
            }
        }
    }

    private class LazyNode(val value: Int, val operation: Operation) : LazySegmentNode<LazyNode, ValueNode, Int, Operation> {
        override fun plus(other: LazyNode): LazyNode {
            return when (other.operation) {
                Operation.Assign -> other
                Operation.Add -> when (this.operation) {
                    Operation.Assign -> LazyNode(this.value + other.value, Operation.Assign)
                    Operation.Add -> LazyNode(this.value + other.value, Operation.Add)
                }
            }
        }

        override fun applyTo(node: ValueNode, range: IntRange): ValueNode {
            return when (operation) {
                Operation.Assign -> ValueNode(range.size * value)
                Operation.Add -> ValueNode(node.value + range.size * value)
            }
        }
    }

    private fun constructTree(arr: IntArray): LazySegmentTree<LazyNode, ValueNode, Int, Operation> {
        return LazySegmentTree(
            array = arr.toTypedArray(),
            nodeConstructor = ::ValueNode,
            lazyNodeConstructor = { value, operation -> LazyNode(value, operation) })
    }

    @Test
    fun testSingleElementArray() {
        val arr = intArrayOf(42)
        val st = constructTree(arr)

        // Query the only element
        assertEquals(42, st.query(0..0).value)

        // Update the only element
        st.update(0..0, 8, Operation.Add)

        // Now element should be 50
        assertEquals(50, st.query(0..0).value)
    }

    @Test
    fun testNoUpdateJustQuery() {
        val arr = intArrayOf(1, 2, 3, 4, 5)
        val st = constructTree(arr)

        // Query entire range
        assertEquals(15, st.query(0..4).value)

        // Query partial range
        assertEquals(9, st.query(1..3).value)  // 2 + 3 + 4
    }

    @Test
    fun testRangeUpdate() {
        val arr = intArrayOf(1, 2, 3, 4, 5)
        val st = constructTree(arr)

        // Initial sum checks
        assertEquals(15, st.query(0..4).value)  // 1+2+3+4+5
        assertEquals(9, st.query(1..3).value)   // 2+3+4

        // Range update: add 10 to [1..3]
        st.update(1..3, 10, Operation.Add)
        // Now the array is effectively [1, (2+10), (3+10), (4+10), 5] => [1,12,13,14,5]

        // Check sums again
        assertEquals(45, st.query(0..4).value)  // 1+12+13+14+5
        assertEquals(39, st.query(1..3).value)  // 12+13+14
    }

    @Test
    fun testMultipleRangeUpdates() {
        val arr = intArrayOf(1, 1, 1, 1, 1)
        val st = constructTree(arr)

        // Initial sum
        assertEquals(5, st.query(0.. 4).value)

        // Add 2 to [0..2]
        st.update(0..2, 2, Operation.Add)
        // New array => [3, 3, 3, 1, 1], sum => 3+3+3+1+1 = 11
        assertEquals(11, st.query(0.. 4).value)

        // Add 4 to [2..4]
        st.update(2..4, 4, Operation.Add)
        // New array => [3, 3, (3+4), (1+4), (1+4)] => [3, 3, 7, 5, 5], sum => 3+3+7+5+5 = 23
        assertEquals(23, st.query(0..4).value)

        // Query [1..3] => 3 + 7 + 5 = 15
        assertEquals(15, st.query(1..3).value)
    }

    @Test
    fun testLargeRangeUpdate() {
        val arr = IntArray(10) { it + 1 }  // [1,2,3,4,5,6,7,8,9,10]
        val st = constructTree(arr)

        // Sum of [0..9] => 55
        assertEquals(55, st.query(0..9).value)

        // Add 5 to entire range [0..9]
        st.update(0..9, 5, Operation.Add)
        // Now effectively => [6,7,8,9,10,11,12,13,14,15], sum => 105
        assertEquals(105, st.query(0..9).value)

        // Add 10 to [3..7]
        st.update(3..7, 10, Operation.Add)
        // Now => [6,7,8, (9+10), (10+10), (11+10), (12+10), (13+10), 14, 15]
        //      => [6,7,8, 19, 20, 21, 22, 23, 14, 15]
        // Sum => 6+7+8+19+20+21+22+23+14+15 = 155
        assertEquals(155, st.query(0..9).value)

        // Partial check: [3..5] => 19+20+21 = 60
        assertEquals(60, st.query(3..5).value)
    }
}