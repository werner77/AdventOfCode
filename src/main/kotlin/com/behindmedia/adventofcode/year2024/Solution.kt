package com.behindmedia.adventofcode.year2024

import java.util.*
import kotlin.collections.ArrayDeque


fun main() {

}

//private class Solution {
//
//    fun findSubstring(s: String, words: Array<String>): List<Int> {
//        // All words are the same length
//        // They can be concatenated in any order
//
//        // TreeSet?
//
//        val wordSet = TreeSet<List<Char>>()
//        for (word in words) {
//            wordSet.add(word.toList())
//        }
//
//        val wordLength = words[0].length
//        val current = ArrayDeque<Char>()
//
//        val matches = Array<MutableSet<Int>>(s.length) { mutableSetOf() }
//
//        for (c in s) {
//            current.add(c)
//            if (current.size == wordLength) {
//                if (wordSet.contains(current)) {
//                    //
//                }
//            }
//            current.removeFirst()
//        }
//    }
//
//}