package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class Day14Test {

    data class Component(val amount: Long, val identifier: String)

    data class Formula(val ingredients: List<Component>, val output: Component) {

        fun addToMap(map: MutableMap<String, Long>, multiplier: Long = 1) {
            for (ingredient in ingredients) {
                val currentValue = map.getOrDefault(ingredient.identifier, 0)
                map[ingredient.identifier] = currentValue + (multiplier * ingredient.amount)
            }
        }
    }

    fun parseInput(string: String): List<Formula> {
        val lines = string.split("\n")
        return lines.map {
            val components = it.split(',', ' ', '=', '>').filter { s -> !s.isBlank() }
            assert(components.size %2 == 0)

            val input = mutableListOf<Component>()
            val output = Component(components[components.size - 2].toLong(), components[components.size - 1])

            for (i in 0 until components.size - 2 step 2) {
                val amount = components[i].toLong()
                val identifier = components[i + 1]
                input.add(Component(amount, identifier))
            }

            Formula(input, output)
        }
    }

    @Test
    fun puzzle1() {
        val result = Day14().oreForSingleFuel(read("/day14.txt"))
        println(result)

        assertEquals(273638, result)
    }

    @Test
    fun puzzle2() {
        val result = Day14().maxAmountOfFuel(read("/day14.txt"))
        println(result)
        assertEquals(4200533, result)
    }

    @Test
    fun sample1() {
        val encoded = """
        9 ORE => 2 A
        8 ORE => 3 B
        7 ORE => 5 C
        3 A, 4 B => 1 AB
        5 B, 7 C => 1 BC
        4 C, 1 A => 1 CA
        2 AB, 3 BC, 4 CA => 1 FUEL    
        """.trimIndent()

        val result = Day14().oreForSingleFuel(encoded)
        assertEquals(165, result)
    }

    @Test
    fun sample2() {
        val encoded = """
        157 ORE => 5 NZVS
        165 ORE => 6 DCFZ
        44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL
        12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ
        179 ORE => 7 PSHF
        177 ORE => 5 HKGWZ
        7 DCFZ, 7 PSHF => 2 XJWVT
        165 ORE => 2 GPVTF
        3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT    
        """.trimIndent()

        val result = Day14().oreForSingleFuel(encoded)
        assertEquals(13312, result)
    }

    @Test
    fun sample3() {
        val encoded = """
        2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG
        17 NVRVD, 3 JNWZP => 8 VPVL
        53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL
        22 VJHF, 37 MNCFX => 5 FWMGM
        139 ORE => 4 NVRVD
        144 ORE => 7 JNWZP
        5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC
        5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV
        145 ORE => 6 MNCFX
        1 NVRVD => 8 CXFTF
        1 VJHF, 6 MNCFX => 4 RFSQX
        176 ORE => 6 VJHF    
        """.trimIndent()

        val result = Day14().oreForSingleFuel(encoded)
        assertEquals(180697, result)
    }
}