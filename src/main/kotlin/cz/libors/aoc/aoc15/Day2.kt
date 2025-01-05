package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines
import kotlin.math.min

@Day("I Was Told There Would Be No Math")
object Day2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input2.txt").map { it.findInts() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<List<Int>>) = input.sumOf { paperNeeded(it)  }
    private fun task2(input: List<List<Int>>) = input.sumOf { ribbonNeeded(it) }

    private fun paperNeeded(box: List<Int>): Int {
        val sides = listOf(box[0] * box[1], box[0] * box[2], box[1] * box[2])
        return 2 * sides[0] + 2 * sides[1]  + 2 * sides[2] + min(min(sides[0], sides[1]), sides[2])
    }

    private fun ribbonNeeded(box: List<Int>): Int {
        val sorted = box.sorted()
        return box[0] * box[1] * box[2] + sorted[0] * 2 + sorted[1] * 2
    }

}