package com.behindmedia.adventofcode2019

class Day4 {

    fun numberOfValidPasswords(minimum: Int, maximum: Int, strict: Boolean): Int {
        var count = 0
        for (number in minimum..maximum) {
            if (isValidPassword(number, strict)) {
                count++
            }
        }
        return count
    }

    fun isValidPassword(password: Int, strict: Boolean): Boolean {

        if (password < 100_000 || password >= 1_000_000) {
            return false
        }

        var lastDigit: Int? = null
        var number = password
        var sameCount = 0
        var foundSame = false
        for(i in 0 until 6) {
            val rightMostDigit = number % 10
            number /= 10
            if (lastDigit != null) {
                if (lastDigit == rightMostDigit) {
                    sameCount++
                    if (!strict) {
                        foundSame = true
                    }
                } else if (rightMostDigit > lastDigit) {
                    return false
                } else {
                    foundSame = foundSame || (sameCount == 1)
                    sameCount = 0
                }
            }
            lastDigit = rightMostDigit
        }
        return foundSame || sameCount == 1
    }
}