package com.behindmedia.adventofcode.year2020.day4

import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.splitWithDelimiters

private class Passport(val fields: Map<String, String>) {
    companion object {
        val required = setOf("ecl", "byr", "iyr", "eyr", "hgt", "hcl", "pid")
    }

    fun isValid(extended: Boolean): Boolean {
        if ((required - fields.keys).isNotEmpty()) {
            return false
        }

        if (!extended) {
            return true
        }

        fields["byr"]?.toIntOrNull()?.takeIf {
            it in 1920..2002
        } ?: return false

        fields["iyr"]?.toIntOrNull()?.takeIf {
            it in 2010..2020
        } ?: return false

        fields["eyr"]?.toIntOrNull()?.takeIf {
            it in 2020..2030
        } ?: return false

        fields["hgt"]?.takeIf {
            val components = it.splitWithDelimiters("in", "cm").filter { value -> value.isNotEmpty() }
            if (components.size != 2) {
                false
            } else {
                val (height, unit) = components
                val intHeight = height.toIntOrNull()
                if (intHeight == null) {
                    false
                } else {
                    when (unit) {
                        "cm" -> intHeight in 150..193
                        "in" -> intHeight in 59..76
                        else -> false
                    }
                }
            }
        } ?: return false

        fields["hcl"]?.takeIf {
            it.startsWith("#") && it.length == 7 && it.substring(1)
                .all { v -> v - '0' in 0..9 || v - 'a' in 0..5 }
        } ?: return false

        fields["ecl"]?.takeIf {
            it in setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
        } ?: return false

        fields["pid"]?.takeIf {
            it.length == 9 && it.all { v -> v - '0' in 0..9 }
        } ?: return false

        return true
    }
}

fun main() {
    val passports = parse(read("/2020/day4.txt"))
    println(passports.count { it.isValid(false) })
    println(passports.count { it.isValid(true) })
}

private fun parse(text: String): List<Passport> {
    val result = mutableListOf<Passport>()
    val sections = text.split("\n\n")
    for (section in sections) {
        val fields = mutableMapOf<String, String>()
        val lines = section.split("\n").filter { it.isNotBlank() }
        for (line in lines) {
            line.split(" ").map { it.split(":") }.forEach { (k, v) ->
                fields[k] = v
            }
        }
        result += Passport(fields)
    }
    return result
}