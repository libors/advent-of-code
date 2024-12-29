package cz.libors.aoc.aoc18

import cz.libors.util.*

@Day("Chronal Coordinates")
object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input6.txt").map { it.findInts().toPoint() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Point>): Int {
        val origPoints = input.mapIndexed { idx, point -> point to idx }.toMap()
        val box = input.boundingBox()
        val closestMap = mutableMapOf<Point, Int>()
        for (x in box.first.x..box.second.x) {
            for (y in box.first.y..box.second.y) {
                val p = Point(x, y)
                closestMap[p] = origPoints[p] ?: closest(p, input)
            }
        }
        val outer = closestMap
            .filterKeys { it.x == box.first.x || it.x == box.second.x || it.y == box.first.y || it.y == box.second.y }
            .values.toSet()
        val counts = closestMap.filterValues { !outer.contains(it) && it != -1 }.values.groupingBy { it }.eachCount()
        return counts.values.max()
    }

    private fun task2(input: List<Point>) = flood(input.boundingBox().center()) {
        it.neighbours().filter { n -> distSum(n, input) < 10000 }
    }.size


    private fun distSum(p: Point, input: List<Point>) = input.sumOf { it.manhattanDistance(p) }

    private fun closest(p: Point, input: List<Point>): Int {
        var min = Int.MAX_VALUE
        var idx = -1
        for (i in input.indices) {
            val dist = p.manhattanDistance(input[i])
            if (dist < min) {
                min = dist
                idx = i
            } else if (dist == min) {
                idx = -1
            }
        }
        return idx
    }
}