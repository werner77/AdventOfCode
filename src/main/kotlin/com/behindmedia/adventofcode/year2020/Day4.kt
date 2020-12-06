package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines

class Day4 {

    fun part1(input: String): Int {
        /*
        byr (Birth Year)
        iyr (Issue Year)
        eyr (Expiration Year)
        hgt (Height)
        hcl (Hair Color)
        ecl (Eye Color)
        pid (Passport ID)
        cid (Country ID)
         */

        var count = 0
        val currentPassport = mutableMapOf<String, String>()

        parseLines(input) {
            if (it.isBlank()) {
                // next password
                if (hasRequiredData(currentPassport)) {
                    count++
                }
                currentPassport.clear()
            } else {
                val components = it.split(" ")
                components.forEach {
                    val keyValue = it.split(":")
                    if (keyValue.size != 2) {
                        println("bla")
                    }
                    currentPassport[keyValue[0]] = keyValue[1]
                }
            }
        }
        if (hasRequiredData(currentPassport)) {
            count++
        }
        return count
    }

    fun part2(input: String): Int {
        /*
        byr (Birth Year)
        iyr (Issue Year)
        eyr (Expiration Year)
        hgt (Height)
        hcl (Hair Color)
        ecl (Eye Color)
        pid (Passport ID)
        cid (Country ID)
         */

        var count = 0
        val currentPassport = mutableMapOf<String, String>()

        parseLines(input) {
            if (it.isBlank()) {
                // next password
                if (hasRequiredData(currentPassport) && hasValidData(currentPassport)) {
                    count++
                }
                currentPassport.clear()
            } else {
                val components = it.split(" ")
                components.forEach {
                    val keyValue = it.split(":")
                    if (keyValue.size != 2) {
                        println("bla")
                    }
                    currentPassport[keyValue[0]] = keyValue[1]
                }
            }
        }
        if (hasRequiredData(currentPassport) && hasValidData(currentPassport)) {
            count++
        }
        return count
    }

    private fun hasRequiredData(passport: Map<String, String>): Boolean {
        val requiredKeys = listOf(
            "byr",
            "iyr",
            "eyr",
            "hgt",
            "hcl",
            "ecl",
            "pid"
        )
        return passport.keys.containsAll(requiredKeys)
    }

    private fun hasValidData(passport: Map<String, String>): Boolean {
        try {
            var valid = true
            val eclValid = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
            val hclRegex = Regex("""#[0-9a-f]{6}""")
            valid = valid && passport["byr"]!!.toInt() in 1920..2002
            valid = valid && passport["iyr"]!!.toInt() in 2010..2020
            valid = valid && passport["eyr"]!!.toInt() in 2020..2030
            valid = valid && passport["pid"]!!.count { it.isDigit() } == 9
            valid = valid && eclValid.contains(passport["ecl"]!!)
            valid = valid && hclRegex.matches(passport["hcl"]!!)
            valid = valid && heightValid(passport["hgt"]!!)
            return valid
        } catch (e: Exception) {
            return false
        }
    }

    private fun heightValid(heightString: String): Boolean {
        try {
            val value = heightString.substring(0, heightString.length - 2).toInt()
            if (heightString.endsWith("in")) {
                return value in 59..76
            } else if (heightString.endsWith("cm")) {
                return value in 150..193
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }
}