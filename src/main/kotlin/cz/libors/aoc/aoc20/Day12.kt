package cz.libors.aoc.aoc20

import cz.libors.util.*

@Day("Rain Risk")
object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input12.txt").map { Pair(it[0], it.findInts()[0]) }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Pair<Char, Int>>): Int {
        var pos = Point(0, 0)
        var dir = Vector.RIGHT
        for (i in input) {
            when (i.first) {
                'N', 'S', 'E', 'W' -> pos += Vector.from(i.first.toString())!! * i.second
                'F' -> pos += (dir * i.second)
                'L' -> repeat(i.second / 90) { dir = dir.turnLeft() }
                'R' -> repeat(i.second / 90) { dir = dir.turnRight() }
            }
        }
        return pos.manhattanDistance(Point(0, 0))
    }

    private fun task2(input: List<Pair<Char, Int>>): Int {
        var pos = Point(0, 0)
        var wp = Vector(10, -1)
        for (i in input) {
            when (i.first) {
                'N', 'S', 'E', 'W' -> wp += Vector.from(i.first.toString())!! * i.second
                'F' -> pos += wp * i.second
                'L', 'R' -> repeat(i.second / 90) { wp = if (i.first == 'L') wp.turnLeft() else wp.turnRight() }
            }
        }
        return pos.manhattanDistance(Point(0, 0))
    }
}