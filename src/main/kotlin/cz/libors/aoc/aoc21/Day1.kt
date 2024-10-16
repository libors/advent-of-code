package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Sonar Sweep")
object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input1.txt").map { it.toInt() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Int>) = input.fold(State(0, Int.MAX_VALUE)) { acc, value ->
        State(if (value > acc.last) acc.increased + 1 else acc.increased, value)
    }.increased

    private fun task2(input: List<Int>): Int {
        var increases = 0
        var last = Int.MAX_VALUE
        for (i in 0..input.size - 3) {
            val sum = input[i] + input[i+1] + input[i+2]
            if (sum > last) increases++
            last = sum
        }
        return increases
    }

    private data class State(val increased: Int, val last: Int)
}