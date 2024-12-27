package com.behindmedia.adventofcode.common

import kotlin.math.max

class PriorityQueue<E>(initialCapacity: Int = DEFAULT_CAPACITY, comparator: Comparator<in E>) : AbstractMutableCollection<E>() {
    private val impl = java.util.PriorityQueue(initialCapacity, comparator)

    companion object {
        const val DEFAULT_CAPACITY = 16

        inline operator fun <reified E: Comparable<E>> invoke(initialCapacity: Int = DEFAULT_CAPACITY): PriorityQueue<E> {
            return PriorityQueue(initialCapacity) { o1, o2 -> o1.compareTo(o2) }
        }

        inline operator fun <reified E: Comparable<E>> invoke(elements: Collection<E>): PriorityQueue<E> {
            return invoke<E>(max(elements.size, DEFAULT_CAPACITY)).also {
                it.addAll(elements)
            }
        }
    }

    override fun add(element: E) = impl.offer(element)

    override val size: Int
        get() = impl.size

    override fun iterator(): MutableIterator<E> {
        return impl.iterator()
    }

    /**
     * Returns the first element, or throws [NoSuchElementException] if this deque is empty.
     */
    fun first(): E = impl.element()

    /**
     * Returns the first element, or `null` if this deque is empty.
     */
    fun firstOrNull(): E? = impl.peek()

    /**
     * Appends the specified [element] to this deque.
     */
    fun addLast(element: E) = impl.offer(element)

    /**
     * Removes the first element from this deque and returns that removed element, or throws [NoSuchElementException] if this deque is empty.
     */
    fun removeFirst(): E = impl.remove()

    /**
     * Removes the first element from this deque and returns that removed element, or returns `null` if this deque is empty.
     */
    fun removeFirstOrNull(): E? = impl.poll()

    override fun addAll(elements: Collection<E>): Boolean = impl.addAll(elements)

    override fun clear() = impl.clear()

    override fun contains(element: E): Boolean = impl.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = impl.containsAll(elements)

    override fun isEmpty(): Boolean = impl.isEmpty()

    override fun remove(element: E): Boolean = impl.remove(element)

    override fun removeAll(elements: Collection<E>): Boolean = impl.removeAll(elements)

    override fun retainAll(elements: Collection<E>): Boolean = impl.retainAll(elements)

    override fun toArray(): Array<Any> = impl.toArray()

    override fun <T : Any?> toArray(a: Array<out T>): Array<T> = impl.toArray(a)

    override fun toString(): String = impl.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherQueue = other as? PriorityQueue<*> ?: return false
        return impl == otherQueue.impl
    }

    override fun hashCode(): Int = impl.hashCode()
}