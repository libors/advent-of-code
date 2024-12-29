package cz.libors.aoc.aoc18

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Chronal Calibration")
object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input1.txt").map { it.toInt() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Int>) = input.fold(0) { a, b -> a + b }

    private fun task2(input: List<Int>): Int {
        var f = 0
        val seen = mutableSetOf(0)
        var i = 0
        while(true) {
            f += input[i++ % input.size]
            if (seen.contains(f)) return f
            seen.add(f)
        }
    }
}