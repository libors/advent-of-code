package cz.libors.aoc.aoc16

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines
import cz.libors.util.toTuples

@Day("Squares With Three Sides")
object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input3.txt").map { it.findInts() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<List<Int>>) = input.count { isTriangle(it)  }

    private fun task2(input: List<List<Int>>) = input
        .flatMap {list -> list.mapIndexed { idx, int -> Pair(idx, int) } }
        .groupBy { it.first }
        .flatMap { it.value }
        .map { it.second }
        .toTuples(3)
        .count { isTriangle(it) }

    private fun isTriangle(sides: List<Int>) = sides[0] + sides[1] > sides[2]
            && sides[1] + sides[2] > sides[0]
            && sides[0] + sides[2] > sides[1]
}