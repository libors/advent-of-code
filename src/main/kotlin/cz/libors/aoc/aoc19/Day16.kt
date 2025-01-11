package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.readToText
import kotlin.math.absoluteValue

@Day("Flawed Frequency Transmission")
object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input16.txt")
        println(task1(input))
        TODO("part2 not working")
        println(task2(input))
    }

    private fun task1(input: String) = encode(input, 100, 0)

    private fun task2(input: String): String {
        val offset = input.substring(0..7).toInt()
        return encode(input.repeat(10000), 100, offset)
    }

    private fun patternize(number: Int, repeat: Int, idx: Int): Int = when (((idx + 1) / repeat) % 4) {
        0, 2 -> 0
        1 -> number
        3 -> -number
        else -> throw RuntimeException()
    }

    private fun encode(input: String, phases: Int, offset: Int): String {
        var numbers = input.map { Character.getNumericValue(it) }.toIntArray()
        for (i in 1..phases)
            numbers = encodePhase(numbers)
        return numbers.copyOfRange(offset, offset + 8).joinToString("") { it.toString() }
    }

    private fun encodePhase(input: IntArray): IntArray {
        val result = IntArray(input.size)
        for (i in 1..input.size) {
            val sum = input.foldIndexed(0) { idx, acc, value -> acc + patternize(value, i, idx) }
            result[i - 1] = sum.absoluteValue % 10
        }
        return result
    }
}