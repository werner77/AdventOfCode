package com.behindmedia.adventofcode.year2017.day16

import com.behindmedia.adventofcode.common.*
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap
import kotlin.math.*

//class State {
//
//    // This array contains the position for each char (a = 0, b = 1, etc)
//    private val chars = CharArray(16) { 'a' + it }
//    private val indexes = IntArray(16) { it }
//    private var offset: Int = 0
//
//    fun swap(amount: Int) {
//        offset = (offset - amount + data.size) % data.size
//    }
//
//    fun exchange(a: Int, b: Int) {
//        val index1 = internalIndex(a)
//        val index2 = internalIndex(b)
//        val char1 = chars[index1] - 'a'
//        val char2 = chars[index2] - 'a'
//
//        chars[index1] = chars[index2].also { chars[index2] = chars[index1] }
//        indexes[char1] = indexes[char2].also {  }
//    }
//
//    fun partner(a: Char, b: Char) {
//        val x = a - 'a'
//        val y = b - 'a'
//        data[x] = data[y].also { data[y] = data[x] }
//    }
//
//    // Internal index always stays the same
//
//    private fun internalIndex(index: Int): Int {
//        return (offset + index + data.size) % data.size
//    }
//
//    private fun externalIndex(index: Int): Int {
//        return (index - offset + data.size) % data.size
//    }
//
//    override fun toString(): String {
//        val buffer = StringBuilder()
//        for (i in 0 until data.size) {
//            val j = (offset + i + data.size) % data.size
//            buffer.append(data[j])
//        }
//        return buffer.toString()
//    }
//}
//
//sealed class Move {
//    abstract fun execute(input: State)
//
//    class Spin(val arg1: Int) : Move() {
//
//        override fun execute(input: State) {
//            input.swap(arg1)
//        }
//    }
//
//    class Exchange(val arg1: Int, val arg2: Int) : Move() {
//        override fun execute(input: State) {
//            input.exchange(arg1, arg2)
//        }
//    }
//
//    class Partner(val arg1: Char, val arg2: Char) : Move() {
//        override fun execute(input: State) {
//            input.partner(arg1, arg2)
//        }
//    }
//}
//
//fun main() {
//    val moves = parse("/2017/day16.txt") { line ->
//        line.split(",").map { instr ->
//            when (instr[0]) {
//                's' -> Move.Spin(instr.substring(1).trim().toInt())
//                'x' -> {
//                    val components = instr.substring(1).trim().split("/").map { it.toInt() }
//                    Move.Exchange(components[0], components[1])
//                }
//                'p' -> {
//                    val components = instr.substring(1).trim().split("/").map { it }
//                    Move.Partner(components[0].single(), components[1].single())
//                }
//                else -> error("Could not parse line: $instr")
//            }
//        }
//    }
//
//    val initialMap = (0 until 16).fold(mutableMapOf<Int, Char>()) { map, value ->
//        map.apply {
//            put(value, 'a' + value)
//        }
//    }
//    val state = State(DualLinkedHashBidiMap(initialMap))
//    for (i in 0L until 1000000000L) {
//        moves.forEach {
//            it.execute(state)
//        }
//    }
//    println(state)
//}