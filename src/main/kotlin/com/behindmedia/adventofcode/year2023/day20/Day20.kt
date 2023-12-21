package com.behindmedia.adventofcode.year2023.day20

import com.behindmedia.adventofcode.common.chineseRemainder
import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.year2023.day20.Module.Broadcast
import com.behindmedia.adventofcode.year2023.day20.Module.Conjunction
import com.behindmedia.adventofcode.year2023.day20.Module.FlipFlop

sealed class Module(val name: String, val destinations: List<String>) {
    abstract fun process(input: String, pulse: Boolean): List<Pair<String, Boolean>>
    abstract fun reset()

    class FlipFlop(name: String, destinations: List<String>) : Module(name, destinations) {
        private var _state: Boolean = false
        val state: Boolean
            get() = _state

        override fun reset() {
            _state = false
        }

        override fun process(input: String, pulse: Boolean): List<Pair<String, Boolean>> {
            return if (pulse) {
                emptyList()
            } else {
                _state = !_state
                return destinations.map { it to state }
            }
        }
    }

    class Conjunction(name: String, destinations: List<String>) : Module(name, destinations) {
        private val _states: MutableMap<String, Boolean> = mutableMapOf()
        val states: Map<String, Boolean>
            get() = _states

        override fun reset() {
            for (name in states.keys.toList()) {
                _states[name] = false
            }
        }

        override fun process(input: String, pulse: Boolean): List<Pair<String, Boolean>> {
            require(_states[input] != null)
            _states[input] = pulse
            val nextPulse = !_states.values.all { it }
            return destinations.map { it to nextPulse }
        }

        fun addInput(name: String) {
            _states[name] = false
        }
    }

    class Broadcast(name: String, destinations: List<String>) : Module(name, destinations) {
        override fun reset() {
        }

        override fun process(input: String, pulse: Boolean): List<Pair<String, Boolean>> {
            return destinations.map { it to pulse }
        }
    }
}

fun main() {
    val modules = parseLines("/2023/day20.txt") { line ->
        val (input, outputsString) = line.split("->").map { it.trim() }
        val outputs = outputsString.split(" ", ",").filter { it.isNotBlank() }
        when {
            input == "broadcaster" -> Broadcast(input, outputs)
            input[0] == '%' -> FlipFlop(input.substring(1), outputs)
            input[0] == '&' -> Conjunction(input.substring(1), outputs)
            else -> error("Invalid type: $input")
        }
    }.associateBy { it.name }

    // Process inputs for conjunction modules
    for ((name, module) in modules) {
        for (target in module.destinations) {
            val targetModule = modules[target] ?: continue
            if (targetModule is Conjunction) {
                targetModule.addInput(name)
            }
        }
    }
    println(part1(modules))
    modules.values.forEach { it.reset() }
    println(part2(modules))
}

private fun part1(modules: Map<String, Module>): Long {
    var lowPulses = 0L
    var highPulses = 0L
    repeat(1000) {
        process(modules) { _, pulse ->
            if (pulse) {
                highPulses++
            } else {
                lowPulses++
            }
        }
    }
    return lowPulses * highPulses
}

private fun part2(modules: Map<String, Module>): Long {
    var iteration = 0L
    val targetModule = modules.values.single { it.destinations == listOf("rx") } as Conjunction
    val initialIterations = mutableMapOf<String, Long>()
    val cycleIterations = mutableMapOf<String, Long>()
    while (initialIterations.size != targetModule.states.size || cycleIterations.size != targetModule.states.size) {
        iteration++
        process(modules) { module, _ ->
            if (module === targetModule) {
                for ((name, pulse) in module.states) {
                    if (pulse) {
                        val initialIteration = initialIterations[name]
                        if (initialIteration == null) {
                            initialIterations[name] = iteration
                        } else if (cycleIterations[name] == null && iteration != initialIteration) {
                            cycleIterations[name] = iteration - initialIteration
                        }
                    }
                }
            }
        }
    }
    return chineseRemainder(targetModule.states.keys.map { initialIterations[it]!! to cycleIterations[it]!! })?.first ?: error("No solution found")
}

private fun process(modules: Map<String, Module>, debug: Boolean = false, onProcess: (Module?, Boolean) -> Unit) {
    val pending = ArrayDeque<Triple<String, String, Boolean>>()
    pending += Triple("broadcaster", "button", false)
    while (pending.isNotEmpty()) {
        val (moduleName, input, pulse) = pending.removeFirst()
        if (debug) {
            println("$input -${if (pulse) "high" else "low"}-> $moduleName")
        }
        val module = modules[moduleName]
        if (module != null) {
            for ((nextModule, nextPulse) in module.process(input, pulse)) {
                pending += Triple(nextModule, moduleName, nextPulse)
            }
        }
        onProcess.invoke(module, pulse)
    }
}