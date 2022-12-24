package com.behindmedia.adventofcode.year2019

class Day1 {

    fun getFuel(mass: Int): Int {
        return mass / 3 - 2
    }

    fun getTotalFuel(masses: List<Int>): Int {
        return masses.sumOf {
            getFuel(it)
        }
    }

    fun cumulativeTotalFuel(masses: List<Int>): Int {
        return masses.sumOf {
            cumulativeFuel(it)
        }
    }

    fun cumulativeFuel(mass: Int): Int {
        var total = 0
        var fuelMass = getFuel(mass)

        while (fuelMass > 0) {
            total += fuelMass
            fuelMass = getFuel(fuelMass)
        }
        return total
    }
}