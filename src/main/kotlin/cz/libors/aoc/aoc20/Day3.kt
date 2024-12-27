package cz.libors.aoc.aoc20

import cz.libors.util.*

@Day("Toboggan Trajectory")
object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input3.txt").toPointsWithValue().toMap()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Map<Point, Char>) = findSlopes(input, 3, 1)

    private fun task2(input: Map<Point, Char>) = listOf(Pair(1, 1), Pair(3, 1), Pair(5, 1), Pair(7, 1), Pair(1, 2))
        .map { findSlopes(input, it.first, it.second) }
        .fold(1L) { a, b -> a * b }

    private fun findSlopes(input: Map<Point, Char>, addX: Int, addY: Int): Int {
        var p = Point(0, 0)
        val box = input.keys.boundingBox()
        val maxY = box.second.y
        val modBase = box.second.x + 1
        var trees = 0
        while (p.y <= maxY) {
            if (input[p] == '#') trees++
            p = Point((p.x + addX) % modBase, p.y + addY)
        }
        return trees
    }
}