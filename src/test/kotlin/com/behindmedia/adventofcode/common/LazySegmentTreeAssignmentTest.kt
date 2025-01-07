package com.behindmedia.adventofcode.common

import kotlin.test.Test
import kotlin.test.assertEquals

class LazySegmentTreeAssignmentTest {

    private data class ValueNode(val value: Int) : SegmentNode<ValueNode, Int> {
        override fun plus(other: ValueNode) = ValueNode(this.value + other.value)
    }

    private data class LazyNode(val value: Int) : LazySegmentNode<LazyNode, ValueNode, Int> {
        override fun plus(other: LazyNode) = LazyNode(this.value + other.value)

        override fun applyTo(
            node: ValueNode,
            range: IntRange
        ) = ValueNode(range.size * value)
    }

    private fun constructTree(arr: IntArray): LazySegmentTree<LazyNode, ValueNode, Int> {
        return LazySegmentTree(
            array = arr.toTypedArray(),
            nodeConstructor = ::ValueNode,
            lazyNodeConstructor = ::LazyNode
        )
    }

    @Test
    fun testSingleElementArray() {
        val arr = intArrayOf(42)
        val st = constructTree(arr)

        // Initially, querying the only element => 42
        assertEquals(42, st.query(0..0).value)

        // Assign 100 to the only element => now should be 100
        st.update(0..0, 100)
        assertEquals(100, st.query(0..0).value)
    }

    @Test
    fun testNoUpdateJustQuery() {
        val arr = intArrayOf(1, 2, 3, 4, 5)
        val st = constructTree(arr)

        // Query entire range => 15
        assertEquals(15, st.query(0..4).value)

        // Query partial range => 2+3+4 = 9
        assertEquals(9, st.query(1..3).value)
    }

    @Test
    fun testFullRangeAssignment() {
        val arr = intArrayOf(1, 2, 3, 4, 5)
        val st = constructTree(arr)

        // Initially => sum(0..4) = 15
        assertEquals(15, st.query(0..4).value)

        // Assign 10 to [0..4] => array effectively becomes [10,10,10,10,10]
        // sum => 10 * 5 = 50
        st.update(0..4, 10)
        assertEquals(50, st.query(0..4).value)
        assertEquals(10, st.query(2..2).value)  // middle element is 10
    }

    @Test
    fun testPartialRangeAssignment() {
        val arr = intArrayOf(1, 2, 3, 4, 5)
        val st = constructTree(arr)

        // Assign 7 to [1..3] => array => [1,7,7,7,5]
        st.update(1..3, 7)

        // sum(0..4) => 1+7+7+7+5 = 27
        assertEquals(27, st.query(0..4).value)
        // sum(1..3) => 7+7+7 = 21
        assertEquals(21, st.query(1..3).value)
        // sum(4..4) => 5
        assertEquals(5, st.query(4..4).value)
    }

    @Test
    fun testMultipleAssignments() {
        val arr = intArrayOf(5, 5, 5, 5, 5)
        val st = constructTree(arr)

        // Initially => sum(0..4) = 25
        assertEquals(25, st.query(0..4).value)

        // Assign 10 to [1..3] => new array => [5,10,10,10,5]
        // sum => 5+10+10+10+5 = 40
        st.update(1..3, 10)
        assertEquals(40, st.query(0..4).value)

        // Assign 2 to [2..4] => new array => [5,10,2,2,2]
        // Overwrites previous values in [2..4] with 2
        // sum => 5+10+2+2+2 = 21
        st.update(2..4, 2)
        assertEquals(21, st.query(0..4).value)

        // Query partial => [1..2] => 10+2 = 12
        assertEquals(12, st.query(1..2).value)
    }

    @Test
    fun testOverlapAssignments() {
        val arr = intArrayOf(0, 0, 0, 0, 0)
        val st = constructTree(arr)

        // Assign 3 to [0..2] => [3,3,3,0,0]
        st.update(0..2, 3)
        assertEquals(9, st.query(0..4).value)

        // Assign 5 to [1..4] => array => [3,5,5,5,5]
        st.update(1..4, 5)
        // The segment [1..2] gets overwritten with 5
        assertEquals(3, st.query(0..0).value) // still 3
        assertEquals(5, st.query(1..1).value) // was 3, now 5
        assertEquals(5, st.query(4..4).value)

        // sum => 3+5+5+5+5 = 23
        assertEquals(23, st.query(0..4).value)
    }
}