package cz.libors.aoc.aoc24

import cz.libors.util.*
import kotlin.math.pow

@Day("Chronospatial Computer")
object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input17.txt").splitByEmptyLine()
        val registers = input[0].splitByNewLine().map { it.findLongs()[0] }
        val program = input[1].findInts()

        println(task1(registers, program))
        println(task2(program))
    }

    private fun task1(registers: List<Long>, program: List<Int>) =
        Computer(registers[0], registers[1], registers[2], program).run().joinToString(",")

    private fun task2(program: List<Int>) = backtrack(0L, 0, program)

    private fun backtrack(a: Long, step: Int, program: List<Int>): Long {
        if (step == program.size) return a
        val expected = Pair(program[program.size - step - 1].toLong(), a)
        var i = 8 * a
        while (i < 8 * (a + 1)) {
            if (runCycle(i, program) == expected) {
                val inner = backtrack(i, step + 1, program)
                if (inner != -1L) return inner
            }
            i++
        }
        return -1
    }

    private fun runCycle(a: Long, program: List<Int>): Pair<Long, Long> {
        return Pair(Computer(a, 0L, 0L, program).run(true)[0].toLong(), a / 8)
    }

    private class Computer(private var a: Long,
                   private var b: Long,
                   private var c: Long,
                   private val program: List<Int>) {
        private val output = mutableListOf<Int>()
        private var instPointer = 0

        private val instructions = listOf<(Int) -> Unit>(
            { a = (a / 2.0.pow(comboOperand(it).toDouble())).toLong() }, // adv
            { b = b xor it.toLong() }, // bxl
            { b = comboOperand(it) % 8 }, // bst
            { if (a != 0L) instPointer = it - 2 }, // jnz
            { b = b xor c }, // bxc
            { output.add((comboOperand(it) % 8).toInt()) }, // out
            { b = (a / 2.0.pow(comboOperand(it).toDouble())).toLong() }, // bdv
            { c = (a / 2.0.pow(comboOperand(it).toDouble())).toLong() }, // cdv
        )

        fun run(oneCycle: Boolean = false): List<Int> {
            while (if (oneCycle) output.isEmpty() else instPointer < program.size) {
                val instruction = instructions[program[instPointer]]
                val operand = program[instPointer + 1]
                instruction(operand)
                instPointer += 2
            }
            return output
        }

        private fun comboOperand(x: Int): Long = when (x) {
            0, 1, 2, 3 -> x.toLong()
            4 -> a
            5 -> b
            6 -> c
            else -> throw IllegalArgumentException("Unexpected op: $x")
        }
    }
}