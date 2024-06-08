package cz.libors.aoc.aoc22

import cz.libors.util.*
import java.lang.Integer.max
import java.lang.Integer.min

@Day(name = "Regolith Reservoir")
object Day14 {
    private val START = Point(500, 0)

    private fun lowestRock(cave: Set<Point>) = cave.sortedWith { a, b -> a.y - b.y }.last().y

    private fun task1(cave: MutableSet<Point>) = dropUntilPossible(cave, lowestRock(cave))

    private fun task2(cave: MutableSet<Point>): Int {
        val lowestRock = lowestRock(cave)
        drawRockLine(Point(-10000, lowestRock + 2), Point(10000, lowestRock + 2), cave)
        return dropUntilPossible(cave, lowestRock + 2) + 1
    }

    private fun drawRockLine(from: Point, to: Point, cave: MutableSet<Point>) {
        if (from.x == to.x) {
            for (i in min(from.y, to.y) until max(from.y, to.y) + 1)
                cave.add(Point(from.x, i))
        } else if (from.y == to.y) {
            for (i in min(from.x, to.x) until max(from.x, to.x) + 1)
                cave.add(Point(i, from.y))
        } else throw IllegalArgumentException()
    }

    private fun dropUntilPossible(cave: MutableSet<Point>, lowestRock: Int): Int {
        var i = 0
        while (!dropSand(START, cave, lowestRock)) i++
        return i
    }

    private fun dropSand(pos: Point, cave: MutableSet<Point>, lowest: Int): Boolean {
        if (pos.y > lowest)
            return true
        if (!cave.contains(Point(pos.x, pos.y + 1)))
            return dropSand(Point(pos.x, pos.y + 1), cave, lowest)
        if (!cave.contains(Point(pos.x - 1, pos.y + 1)))
            return dropSand(Point(pos.x - 1, pos.y + 1), cave, lowest)
        if (!cave.contains(Point(pos.x + 1, pos.y + 1)))
            return dropSand(Point(pos.x + 1, pos.y + 1), cave, lowest)
        if (pos == Point(500, 0)) return true
        cave.add(pos)
        return false
    }

    private fun buildCave(rockSchema: List<List<Point>>): Set<Point> {
        val cave = mutableSetOf<Point>()
        for (lines in rockSchema)
            for (p in 1 until lines.size)
                drawRockLine(lines[p - 1], lines[p], cave)
        return cave.toSet()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input14.txt")
            .map { it.findInts().toTuples(2).map { x -> x.toPoint() } }
        val cave = buildCave(input)
        println(task1(cave.toMutableSet()))
        println(task2(cave.toMutableSet()))
    }

}