package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.findLongs
import cz.libors.util.readToText
import java.lang.RuntimeException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import kotlin.system.exitProcess

@Day("Springdroid Adventure")
object Day21 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input21.txt").findLongs().toLongArray()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(code: LongArray): Long {
        val instructions = listOf(
            "NOT C J", "AND D J",
            "NOT A T", "OR T J",
            "WALK"
        )
        return runComputer(code.copyOf(), instructions)
        // return findInstructions(code, 4, "WALK", 4)
    }

    private fun task2(code: LongArray): Long {
        val instructions = listOf(
            "NOT B J", "NOT C T",
            "OR T J",  "AND D J",
            "AND H J", "NOT A T",
            "OR T J",  "RUN"
        )
        return runComputer(code.copyOf(), instructions)
        // return findInstructions(code, 9, "RUN", 7)
    }

    private fun runComputer(code: LongArray, instructions: List<String>, fast: Boolean = false): Long {
        val input = createInstructions(instructions)
        val output = Computer.create(code, Input.queue(input), memory = ComputerImpl.ArrayMemory(code)).runCode().getOutput()
        val decoded = decode(output)
        if (decoded.contains("Didn't make it across")) {
            if (fast) return -1L else print(decode(output)).also { throw RuntimeException("Fell into space") }
        } else {
            return output.last()
        }
    }

    private fun decode(encoded: List<Long>): String =
        encoded.map { if (it > 250) '?' else it.toChar() }.joinToString("")

    private fun createInstructions(list: List<String>): BlockingQueue<Long> {
        val queue = ArrayBlockingQueue<Long>(1000)
        for (string in list) {
            string.toCharArray().forEach { queue.add(it.toLong()) }
            queue.add(10)
        }
        return queue
    }

    private fun checkValid( // eliminate some variants to try to run on computer
        generator: VariantGenerator,
        instructionList: Array<SpringInstruction>,
        forwardPlaces: Int
    ): Boolean {
        val posOps = IntArray(forwardPlaces)
        posOps[0] = 1 // because NOT A T
        var tCount = 0
        for (i in 0 until generator.elements) {
            val op = instructionList[generator[i]].op1
            if (op < forwardPlaces) {
                if (posOps[op] == 1) {
                    generator.skip(i)
                    return false
                } else posOps[op] = 1
            } else if (op == SpringConst.T_POS) tCount++
        }
        if (tCount == 0) return false
        return true
    }

    private fun findInstructions( // find spring program using brute force
        code: LongArray,
        forwardPlaces: Int,
        lastInstruction: String,
        startNumInstructions: Int
    ): Long {
        val threads = Runtime.getRuntime().availableProcessors() - 1
        val executor = Executors.newFixedThreadPool(threads)
        val semaphore = Semaphore(threads * 10)
        val instructionList = generateInstructions(forwardPlaces)
        var triedOnComputer = 0
        var skipped = 0
        var posNumber = 0
        for (numberOfInstructions in ((startNumInstructions - 2)..15)) {
            println("finding possibilities having ${numberOfInstructions + 2} instructions")
            val generator = VariantGenerator(numberOfInstructions, instructionList.size)
            val start = System.currentTimeMillis()
            while (generator.next()) {
                if (posNumber++ % 100000 == 0) {
                    val time = System.currentTimeMillis() - start
                    println("Skipped ${skipped / 1000}k, tried ${triedOnComputer / 1000}k in ${time / 1000} seconds")
                }
                val valid = checkValid(generator, instructionList, forwardPlaces)
                if (valid) {
                    triedOnComputer++
                    val instructions = ArrayList<String>(numberOfInstructions + 3)
                    for (i in 0 until numberOfInstructions) instructions.add(instructionList[generator[i]].string)
                    instructions.add("NOT A T")
                    instructions.add("OR T J")
                    instructions.add(lastInstruction)
                    semaphore.acquire()
                    executor.submit {
                        val result = runComputer(code.copyOf(), instructions, true)
                        semaphore.release()
                        if (result != -1L) {
                            instructions.forEach { println(it) }
                            exitProcess(1)
                        }
                    }
                } else skipped++
            }
        }
        return -1
    }

    data class SpringInstruction(val instr: Int, val op1: Int, val op2: Int, val string: String)

    object SpringConst {
        const val NOT = 0
        const val OR = 1
        const val AND = 2
        const val START_POS = 'A'.code
        const val T_POS = 'T'.code - START_POS

        fun opInt(s: String) = when (s) {
            "NOT" -> NOT
            "OR" -> OR
            "AND" -> AND
            else -> throw RuntimeException("Unknown instruction")
        }
    }

    private fun generateInstructions(forwardPlaces: Int): Array<SpringInstruction> {
        val result = ArrayList<SpringInstruction>()
        for (name in arrayOf("NOT", "OR", "AND"))
            for (first in (0 until forwardPlaces).map { 'A' + it } + arrayOf('J', 'T'))
                for (second in arrayOf('J', 'T'))
                    if (first != second)
                        result.add(
                            SpringInstruction(
                                SpringConst.opInt(name), first.code - 'A'.code,
                                second.code - 'A'.code, "$name $first $second"
                            )
                        )
        return result.toTypedArray()
    }

    private class VariantGenerator(val elements: Int, val max: Int) {
        private val counters: Array<Counter> = Array(elements) { Counter(1, null) }
        private var nextCalls = 0L
        private val total = Math.pow(max.toDouble(), elements.toDouble()).toLong()
        private val last: Counter

        private var ready = true

        init {
            val first = Counter(max, null)
            counters[0] = first
            for (i in 1 until elements) counters[i] = Counter(max, counters[i - 1])
            last = counters[elements - 1]
        }

        fun skip(position: Int) {
            ready = true
            counters[position].increment()
            for (i in position + 1 until elements)
                counters[i].c = 0
        }

        fun next(): Boolean {
            if (ready) {
                ready = false
            } else {
                last.increment()
            }
            nextCalls++
            return if (last.c != 0) true else checkEnd()
        }

        private fun checkEnd(): Boolean {
            for (i in (elements - 2) downTo 0)
                if (counters[i].c != 0) return true
            return nextCalls == 1L
        }

        fun nextCalls() = nextCalls
        fun total() = total

        operator fun get(i: Int): Int = counters[i].c
    }

    private class Counter(val max: Int, val dependent: Counter?) {
        var c = 0

        fun increment() {
            if (++c == max) {
                c = 0
                dependent?.increment()
            }
        }
    }
}