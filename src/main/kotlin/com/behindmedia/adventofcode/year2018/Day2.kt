package com.behindmedia.adventofcode.year2018

class Day2 {

    fun calculateChecksum(ids: List<String>): Long {
        var countOfTwos = 0L
        var countOfThrees = 0L
        for (id in ids) {
            val checksum = checksum(id)
            if (checksum.first) {
                countOfTwos++
            }
            if (checksum.second) {
                countOfThrees++
            }
        }
        return countOfTwos * countOfThrees
    }

    private fun checksum(id: String): Pair<Boolean, Boolean> {
        val counter = mutableMapOf<Char, Int>()
        var countOfTwo = 0
        var countOfThree = 0
        for (c in id.toCharArray()) {
            val newCount = (counter[c] ?: 0) + 1
            counter[c] = newCount
            when (newCount) {
                2 -> countOfTwo++
                3 -> {
                    countOfTwo--
                    countOfThree++
                }
                4 -> countOfThree--
            }
        }
        return Pair(countOfTwo > 0, countOfThree > 0)
    }

    fun findCommon(ids: List<String>): String? {
        val commonPair = findCommonPair(ids) ?: return null
        return eliminateDuplicateChars(commonPair.first, commonPair.second)
    }

    private fun eliminateDuplicateChars(s1: String, s2: String): String {
        var ret = ""
        for (i in 0 until Math.min(s1.length, s2.length)) {
            val char1 = s1[i]
            val char2 = s2[i]
            if (char1 == char2) {
                ret += char1
            }
        }
        return ret
    }

    private fun findCommonPair(ids: List<String>): Pair<String, String>? {
        for (id1 in ids) {
            for (id2 in ids) {
                if (id1 !== id2 && isCommon(id1, id2)) {
                    return Pair(id1, id2)
                }
            }
        }
        return null
    }

    private fun isCommon(id1: String, id2: String): Boolean {
        if (id1.length != id2.length) {
            return false
        }

        var diffCount = 0
        for (i in 0 until id1.length) {
            val char1 = id1.get(i)
            val char2 = id2.get(i)
            if (char1 != char2) {
                diffCount++
            }
            if (diffCount > 1) {
                return false
            }
        }
        return diffCount == 1
    }

}