package cz.libors.aoc.aoc23

import cz.libors.util.*

@Day(name = "Cosmic Expansion")
object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input11.txt")
            .toPointsWithValue()
            .filter { x -> x.second == '#' }.map { it.first }

        println(task1(input))
        println(task2(input))
    }

    private fun task1(galaxies: List<Point>) = expandAndCount(galaxies, 1)
    private fun task2(galaxies: List<Point>) = expandAndCount(galaxies, 1000000 - 1)

    private fun expandAndCount(galaxies: List<Point>, expandFactor: Int): Long {
        val expanded = expand(galaxies, expandFactor)
        var sum = 0L
        for (i in expanded.indices)
            for (j in i + 1 until expanded.size)
                sum += expanded[i].manhattanDistance(expanded[j])
        return sum
    }

    private fun expand(galaxies: List<Point>, factor: Int): List<Point> {
        val box = galaxies.boundingBox()
        val emptyLines = (box.first.y..box.second.y).filter { galaxies.none { galaxy -> galaxy.y == it } }
        val lineExpanded = galaxies.map { g ->
            val moveBy = emptyLines.count { it < g.y } * factor
            g.plus(Vector.DOWN * moveBy)
        }
        val emptyColumns = (box.first.x..box.second.x).filter { lineExpanded.none { galaxy -> galaxy.x == it } }
        val expanded = lineExpanded.map { g ->
            val moveBy = emptyColumns.count { it < g.x } * factor
            g.plus(Vector.RIGHT * moveBy)
        }
        return expanded
    }
}