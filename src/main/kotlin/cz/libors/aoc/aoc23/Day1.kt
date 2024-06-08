package cz.libors.aoc.aoc23

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day(name = "Trebuchet?!")
object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input1.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>) = input
        .map { line -> line.first { it.isDigit() }.toString() + line.last { it.isDigit() }.toString() }
        .sumOf { it.toInt() }

    private fun task2(input: List<String>) = input.sumOf { getNum(it) }

    private fun getNum(s: String): Int {
        val nums = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
        val minIdx = nums.mapIndexed { idx, value -> Pair(idx, s.indexOf(value)) }
            .filter { it.second >= 0 }
            .minByOrNull { it.second }!!.first
        val maxIdx = nums.mapIndexed { idx, value -> Pair(idx, s.lastIndexOf(value)) }
            .filter { it.second >= 0 }
            .maxByOrNull { it.second }!!.first
        val first = if (minIdx <= 9) minIdx else minIdx - 9
        val second = if (maxIdx <= 9) maxIdx else maxIdx - 9
        return (first.toString() + second.toString()).toInt()
    }
}