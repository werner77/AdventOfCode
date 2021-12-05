package com.behindmedia.adventofcode.year2016.day5

import com.behindmedia.adventofcode.common.read
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

private fun part1() {
    val input = read("/2016/day5.txt")

    var i = 0
    var password = ""

    while (password.length < 8) {
        val c = input + i++.toString()
        val md5 = md5(c)
        val p = passwordChar(md5) ?: continue
        password += p
    }
    println(password)
}

private fun part2() {
    val input = read("/2016/day5.txt")
    var i = 0
    val password = TreeMap<Int, Char>()
    while (password.size < 8) {
        val c = input + i++.toString()
        val md5 = md5(c)
        val p = passwordChar2(md5) ?: continue
        if (password[p.first] == null) {
            password[p.first] = p.second
        }
    }
    val p = password.entries.map { it.value }.joinToString("")
    println(p)
}

private fun passwordChar2(hash: String): Pair<Int, Char>? {
    return if (hash.startsWith("00000")) {
        val pos = (hash[5] - '0')
        val ch = hash[6]

        if (pos in 0 until 8)
            Pair(pos, ch)
        else
            null
    } else {
        null
    }
}

private fun passwordChar(hash: String): Char? {
    return if (hash.startsWith("00000")) {
        hash[5]
    } else {
        null
    }
}

private val md = MessageDigest.getInstance("MD5")

private fun md5(input: String) : String {
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

fun main() {
    part1()
    part2()
}