package com.behindmedia.adventofcode.common

class StringView(private val s: CharSequence, private val startIndex: Int, private val endIndex: Int): CharSequence {

    override fun get(index: Int): Char {
        return s[startIndex + index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        if (startIndex < 0) throw IndexOutOfBoundsException()
        if (endIndex < 0) throw IndexOutOfBoundsException()
        if (endIndex < startIndex) throw IndexOutOfBoundsException()
        if (endIndex > length) throw IndexOutOfBoundsException()
        return StringView(s, this.startIndex + startIndex, this.startIndex + endIndex)
    }

    override val length: Int
        get() = endIndex - startIndex

    override fun toString(): String {
        return s.substring(startIndex, endIndex)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is String) return false
        if (other.length != length) return false
        for (i in 0 until length) {
            if (other[i] != this[i]) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 0
        for (i in 0 until length) {
            result = 31 * result + this[i].code
        }
        return result
    }
}