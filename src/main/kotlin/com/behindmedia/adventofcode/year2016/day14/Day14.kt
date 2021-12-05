package com.behindmedia.adventofcode.year2016.day14

import com.behindmedia.adventofcode.common.popFirst
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.collections.ArrayDeque

private val md = MessageDigest.getInstance("MD5")

private fun md5(input: String) : String {
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

//private val SALT = "abc"
private val SALT = "cuanljph"

private fun hash(index: Long, count: Int = 1): String {
    var s = SALT + index.toString()
    for (i in 0 until count) {
        s = md5(s)
    }
    return s
}

private fun String.test(consecutive: Int, findChar: Char? = null): Char? {
    // Find first triplet
    val buffer = ArrayDeque<Char>()
    for (c in this) {
        if (buffer.size == consecutive) {
            buffer.popFirst()
        }
        buffer.add(c)
        if (buffer.size == consecutive && buffer.all { it == (findChar ?: c) }) {
            return c
        }
    }
    return null
}

fun main() {
    // Store the char to test for and the latest end index
    val candidates = LinkedList<Pair<Char, Long>>()
    val matches = sortedSetOf<Long>()
    var index = 0L

    outer@while(matches.size < 64) {
        val h = hash(index, 2017)
        val c = h.test(3)
        if (c != null) {
            // found three same chars
            val iterator = candidates.iterator()
            while(iterator.hasNext()) {
                val c1 = iterator.next()
                if (index > c1.second + 1000L) {
                    // Candidate not valid anymore
                    iterator.remove()
                } else if (h.test(5, c1.first) != null) {
                    // Found match!
                    matches.add(c1.second)
                    if (matches.size == 64) break@outer
                }
            }
            // Store this as candidate
            candidates.add(Pair(c, index))
        }
        index++
    }
    println(matches.last())
}