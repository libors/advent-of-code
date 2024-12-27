package cz.libors.aoc.aoc20

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Binary Boarding")
object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input5.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>) = input.maxOf { seatId(it) }

    private fun task2(input: List<String>) = input.map { seatId(it) }.sorted()
        .zipWithNext()
        .find { it.second - it.first == 2 }!!
        .let { it.second - 1 }


    private fun seatId(s: String) = Pair(
        s.substring(0, 7).replace('F', '0').replace('B', '1').toInt(2),
        s.substring(7).replace('R', '1').replace('L', '0').toInt(2)
    ).let { it.first * 8 + it.second }
}