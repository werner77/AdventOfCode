package com.behindmedia.adventofcode.common

import kotlin.math.max

interface Queue<E>: MutableCollection<E> {
    /**
     * Returns the first element, or throws [NoSuchElementException] if this deque is empty.
     */
    fun first(): E

    /**
     * Returns the first element, or `null` if this deque is empty.
     */
    fun firstOrNull(): E?

    /**
     * Appends the specified [element] to this deque.
     */
    fun addLast(element: E)

    /**
     * Removes the first element from this deque and returns that removed element, or throws [NoSuchElementException] if this deque is empty.
     */
    fun removeFirst(): E

    /**
     * Removes the first element from this deque and returns that removed element, or returns `null` if this deque is empty.
     */
    fun removeFirstOrNull(): E?
}

class FifoQueue<E>(initialCapacity: Int): Queue<E> {
    constructor(): this(DEFAULT_CAPACITY)
    constructor(elements: Collection<E>): this(DEFAULT_CAPACITY) {
        this.addAll(elements)
    }

    private val impl: ArrayDeque<E> = ArrayDeque(initialCapacity)

    companion object {
        const val DEFAULT_CAPACITY = 16
    }

    override fun first(): E {
        return impl.first()
    }

    override fun firstOrNull(): E? {
        return impl.firstOrNull()
    }

    override fun addLast(element: E) {
        return impl.addLast(element)
    }

    override fun removeFirst(): E {
        return impl.removeFirst()
    }

    override fun removeFirstOrNull(): E? {
        return impl.removeFirstOrNull()
    }

    override fun add(element: E): Boolean {
        return impl.add(element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        return impl.addAll(elements)
    }

    override fun clear() {
        return impl.clear()
    }

    override fun iterator(): MutableIterator<E> {
        return impl.iterator()
    }

    override fun remove(element: E): Boolean {
        return impl.remove(element)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return impl.removeAll(elements)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return impl.retainAll(elements)
    }

    override val size: Int
        get() = impl.size

    override fun isEmpty(): Boolean {
        return impl.isEmpty()
    }

    override fun contains(element: E): Boolean {
        return impl.contains(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return impl.containsAll(elements)
    }

    override fun toString(): String {
        return impl.toString()
    }
}

class SortedQueue<E>(initialCapacity: Int, comparator: Comparator<in E>) : Queue<E>, AbstractMutableCollection<E>() {
    constructor(comparator: Comparator<in E>) : this(DEFAULT_CAPACITY, comparator)

    private val impl = java.util.PriorityQueue(initialCapacity, comparator)

    companion object {
        const val DEFAULT_CAPACITY = 16

        inline operator fun <reified E: Comparable<E>> invoke(initialCapacity: Int = DEFAULT_CAPACITY): SortedQueue<E> {
            return SortedQueue(initialCapacity) { o1, o2 -> o1.compareTo(o2) }
        }

        inline operator fun <reified E: Comparable<E>> invoke(elements: Collection<E>): SortedQueue<E> {
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
    override fun first(): E = impl.element()

    /**
     * Returns the first element, or `null` if this deque is empty.
     */
    override fun firstOrNull(): E? = impl.peek()

    /**
     * Appends the specified [element] to this deque.
     */
    override fun addLast(element: E) {
        impl.offer(element)
    }

    /**
     * Removes the first element from this deque and returns that removed element, or throws [NoSuchElementException] if this deque is empty.
     */
    override fun removeFirst(): E = impl.remove()

    /**
     * Removes the first element from this deque and returns that removed element, or returns `null` if this deque is empty.
     */
    override fun removeFirstOrNull(): E? = impl.poll()

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
}