package cz.libors.aoc.aoc23

import cz.libors.util.Day
import cz.libors.util.lcm
import cz.libors.util.readToLines
import java.util.LinkedList

@Day(name = "Pulse Propagation")
object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input20.txt").map {
            val split = it.split(" -> ")
            val name = if (split[0] == "broadcaster") split[0] else split[0].substring(1)
            val type = if (split[0] == "broadcaster") 'b' else split[0][0]
            val outputs = split[1].split(", ")
            createModule(type, name, outputs)
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Module>): Long {
        val device = Device(input.associateBy { it.name })
        repeat(1000) { device.pushButton() }
        return device.score()
    }

    private fun task2(input: List<Module>): Long {
        val device = Device(input.associateBy { it.name })
        val rxConjunction = input.first { it is Conjunction && it.outputs.contains("rx") } as Conjunction
        val highSignalCatcher = HighSignalCatcher(rxConjunction.inputs())
        rxConjunction.registerCallback(highSignalCatcher::update)
        do {
            device.pushButton()
        } while (!highSignalCatcher.isDone())
        return highSignalCatcher.compute()
    }

    private fun createModule(type: Char, name: String, outputs: List<String>) = when (type) {
        'b' -> Broadcaster(name, outputs)
        '%' -> FlipFlop(name, outputs)
        '&' -> Conjunction(name, outputs)
        else -> throw IllegalArgumentException()
    }

    private class Device(val modules: Map<String, Module>) {
        private val queue = LinkedList<Pulse>()
        private var lowPulses = 0
        private var highPulses = 0
        private var presses = 0

        init {
            modules.values.forEach {
                if (it is Conjunction) {
                    modules.values
                        .filter { m -> m.outputs.contains(it.name) }
                        .forEach { output -> it.registerInput(output.name) }
                }
            }
        }

        fun pushButton() {
            queue.add(Pulse("button", "broadcaster", false, ++presses))
            processQueue()
        }

        private fun processQueue() {
            while (queue.isNotEmpty()) {
                val pulse = queue.removeFirst()
                if (pulse.high) highPulses++ else lowPulses++
                val targetModule = modules[pulse.target]
                if (targetModule != null) {
                    queue.addAll(targetModule.process(pulse))
                }
            }
        }

        fun score(): Long = lowPulses.toLong() * highPulses
    }

    private data class Pulse(val source: String, val target: String, val high: Boolean, val buttonPress: Int) {
        override fun toString() = "$source ${if (high) "high" else "low"} -> $target"
    }

    private abstract class Module(val name: String, val outputs: List<String>) {
        abstract fun process(pulse: Pulse): List<Pulse>
    }

    private class Broadcaster(name: String, outputs: List<String>) : Module(name, outputs) {
        override fun process(pulse: Pulse): List<Pulse> = outputs.map { Pulse(name, it, pulse.high, pulse.buttonPress) }
    }

    private class Conjunction(name: String, outputs: List<String>) : Module(name, outputs) {
        private var last = mutableMapOf<String, Boolean>()
        private var callback: (Map<String, Boolean>, Int) -> Unit = {_, _ -> }

        fun registerInput(name: String) {
            last[name] = false
        }

        fun registerCallback(callback: (Map<String, Boolean>, Int) -> Unit) {
            this.callback = callback
        }

        override fun process(pulse: Pulse): List<Pulse> {
            last[pulse.source] = pulse.high
            callback(last, pulse.buttonPress)
            val allHigh = last.values.all { it }
            return outputs.map { Pulse(name, it, !allHigh, pulse.buttonPress) }
        }

        fun inputs() = last.size
    }

    private class FlipFlop(name: String, ouputs: List<String>) : Module(name, ouputs) {
        private var state = false

        override fun process(pulse: Pulse) = if (pulse.high) emptyList() else {
            state = !state
            outputs.map { Pulse(name, it, state, pulse.buttonPress) }
        }
    }

    private class HighSignalCatcher(val expectedSize: Int) {
        val periods = mutableMapOf<String, Int>()

        fun update(input: Map<String, Boolean>, pressCount: Int) {
            input.filter { it.value }.forEach { periods.putIfAbsent(it.key, pressCount) }
        }

        fun isDone() = periods.size == expectedSize
        fun compute()= periods.values.map { it.toLong() }.reduce { a, b -> lcm(a, b) }
    }
}