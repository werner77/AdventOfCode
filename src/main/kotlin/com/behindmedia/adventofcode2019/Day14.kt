package com.behindmedia.adventofcode2019

class Day14 {

    data class Component(val amount: Long, val identifier: String)

    data class Formula(val ingredients: List<Component>, val output: Component) {

        fun addToMap(map: MutableMap<String, Long>, multiplier: Long = 1) {
            for (ingredient in ingredients) {
                val currentValue = map.getOrDefault(ingredient.identifier, 0)
                map[ingredient.identifier] = currentValue + (multiplier * ingredient.amount)
            }
        }
    }

    fun parseInput(string: String): Map<String, Formula> {
        val lines = string.split("\n")
        val formulas = lines.map {
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

        return formulas.fold(mutableMapOf()) { map, formula ->
            assert(map[formula.output.identifier] == null)
            map[formula.output.identifier] = formula
            map
        }
    }

    fun oreForSingleFuel(input: String): Long {
        return oreForFuel(parseInput(input), 1L)
    }

    fun oreForFuel(formulaMap: Map<String, Formula>, fuelAmount: Long): Long {
        val totalComponentsNeeded = mutableMapOf<String, Long>()
        val fuelFormula = formulaMap["FUEL"] ?: throw IllegalStateException("No fuel found")
        assert(fuelFormula.output.amount == 1L)
        fuelFormula.addToMap(totalComponentsNeeded, fuelAmount)

        while (true) {
            // Find the components needed

            val firstEntry = totalComponentsNeeded.entries.find { it.key != "ORE" && it.value > 0 } ?: break

            val identifier = firstEntry.key
            val amount = firstEntry.value

            //Find the input
            val formulaNeeded = formulaMap[identifier] ?: throw IllegalStateException("No formula found for $identifier")

            // Try to consume leftovers

            val formulaCount =
                (amount / formulaNeeded.output.amount) + (if (amount % formulaNeeded.output.amount == 0L) 0L else 1L)

            val leftOverAmount = formulaCount * formulaNeeded.output.amount - amount

            totalComponentsNeeded[identifier] = -leftOverAmount

            // Add the inputs of that formula to the map
            formulaNeeded.addToMap(totalComponentsNeeded, formulaCount)
        }
        return totalComponentsNeeded["ORE"] ?: 0
    }

    fun maxAmountOfFuel(input: String, amountOfOre: Long = 1000000000000L): Long {
        return maxAmountOfFuel(parseInput(input), amountOfOre)
    }

    fun maxAmountOfFuel(formulaMap: Map<String, Formula>, amountOfOre: Long): Long {

        val oreForSingleFuel = oreForFuel(formulaMap, 1)

        val lowerBound = amountOfOre / oreForSingleFuel
        val upperBound = 2 * lowerBound

        return binarySearch(formulaMap, lowerBound, upperBound, amountOfOre)
    }

    private fun binarySearch(formulaMap: Map<String, Formula>, lowerBound: Long, upperBound: Long, maxAmountOre: Long): Long {
        var begin = lowerBound
        var end = upperBound
        var result: Long? = null
        while (begin <= end) {
            val mid = (begin + end) / 2L
            if (oreForFuel(formulaMap, mid) <= maxAmountOre) {
                result = mid
                begin = mid + 1
            } else {
                end = mid - 1
            }
        }
        return result ?: throw IllegalStateException("No result found for specified bounds")
    }
}