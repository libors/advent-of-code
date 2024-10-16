package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.Point
import cz.libors.util.findInts
import cz.libors.util.readToLines
import kotlin.math.abs
import kotlin.math.max

@Day("Hydrothermal Venture")
object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input5.txt").map { it.findInts() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<List<Int>>) = countIntersections(input.filter { it[0] == it[2] || it[1] == it[3] })
    private fun task2(input: List<List<Int>>): Int = countIntersections(input)

    private fun countIntersections(input: List<List<Int>>): Int {
        val field = mutableMapOf<Point, Int>()
        for ((x1, y1, x2, y2) in input) {
            val xInc = increment(x1, x2)
            val yInc = increment(y1, y2)
            val steps = max(abs(x1 - x2), abs(y1 - y2)) + 1
            var x = x1
            var y = y1
            for (i in 1..steps) {
                field.merge(Point(x, y), 1, Int::plus)
                x += xInc
                y += yInc
            }
        }
        return field.count { it.value > 1 }
    }

    private fun increment(a: Int, b: Int) = when {
        a == b -> 0
        a < b -> 1
        else -> -1
    }
}