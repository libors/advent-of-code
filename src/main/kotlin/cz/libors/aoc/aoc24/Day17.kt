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
        Computer(registers.toMutableList(), program).run().joinToString(",")

    private fun task2(program: List<Int>) = backtrack(0L, 0, program)

    private fun backtrack(a: Long, step: Int, program: List<Int>): Long {
        if (step == program.size) return a
        val expected = Pair(program[program.size - step - 1].toLong(), a)
        var i = 8 * a
        while (i < 8 * (a + 1)) {
            if (runCycle(i) == expected) {
                val inner = backtrack(i, step + 1, program)
                if (inner != -1L) return inner
            }
            i++
        }
        return -1
    }

    // ! this is transcription of my input, not a general solution to all inputs
    private fun runCycle(a: Long): Pair<Long, Long> {
        var b = a % 8
        b = b xor 2
        val c = a / (2.0.pow(b.toDouble())).toLong()
        b = b xor 7
        b = b xor c
        return Pair(b % 8, a / 8)
    }

    class Computer(private val registers: MutableList<Long>, private val program: List<Int>) {
        private val output = mutableListOf<Int>()
        private var instPointer = 0

        private val instructions = listOf<(Int) -> Unit>(
            { registers[0] = (registers[0] / 2.0.pow(comboOperand(it).toDouble())).toLong() }, // adv
            { registers[1] = registers[1] xor it.toLong() }, // bxl
            { registers[1] = comboOperand(it) % 8 }, // bst
            { if (registers[0] != 0L) instPointer = it - 2 }, // jnz
            { registers[1] = registers[1] xor registers[2] }, // bxc
            { output.add((comboOperand(it) % 8).toInt()) }, // out
            { registers[1] = (registers[0] / 2.0.pow(comboOperand(it).toDouble())).toLong() }, // bdv
            { registers[2] = (registers[0] / 2.0.pow(comboOperand(it).toDouble())).toLong() }, // cdv
        )

        fun run(): List<Int> {
            while (instPointer < program.size) {
                val instruction = instructions[program[instPointer]]
                val operand = program[instPointer + 1]
                instruction(operand)
                instPointer += 2
            }
            return output
        }

        private fun comboOperand(x: Int): Long = when (x) {
            0, 1, 2, 3 -> x.toLong()
            4, 5, 6 -> registers[x - 4]
            else -> throw IllegalArgumentException("Unexpected op: $x")
        }
    }
}