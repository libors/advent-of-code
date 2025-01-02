package cz.libors.aoc.aoc16

import cz.libors.util.*

@Day("No Time for a Taxicab")
object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input1.txt").split(", ")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>) = go(input)
    private fun task2(input: List<String>) = go(input, true)

    private fun go(input: List<String>, stopAtVisited: Boolean = false): Int {
        val visited = mutableSetOf<Point>()
        var pos = Point(0, 0)
        var vector = Vector.UP
        for (instruction in input) {
            vector = if (instruction[0] == 'L') vector.turnLeft() else vector.turnRight()
            val num = instruction.substring(1).toInt()
            for (x in 1..num) {
                pos += vector
                if (stopAtVisited) {
                    if (visited.contains(pos)) return pos.manhattanDistance(Point(0, 0))
                    visited.add(pos)
                }
            }
        }
        return pos.manhattanDistance(Point(0, 0))
    }
}