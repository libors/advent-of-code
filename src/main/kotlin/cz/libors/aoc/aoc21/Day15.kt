package cz.libors.aoc.aoc21

import cz.libors.util.*

@Day("Chiton")
object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {
        val points = readToLines("input15.txt").toPointsWithValue().associate { it.first to it.second.digitToInt() }
        println(task1(points))
        println(task2(points))
    }

    private fun task1(points: Map<Point, Int>) = findPath(points)

    private fun task2(points: Map<Point, Int>): Int {
        val box = points.keys.boundingBox()
        val (maxX, maxY) = box.size()
        val bigPoints = mutableMapOf<Point, Int>()
        for (x in 0 until maxX * 5)
            for (y in 0 until maxY * 5) {
                val add = x / maxX + y / maxY
                val origPoint = points[Point(x % maxX, y % maxY)]!!
                bigPoints[Point(x, y)] = (origPoint - 1 + add) % 9 + 1
            }
        return findPath(bigPoints)
    }

    private fun findPath(points: Map<Point, Int>) = points.keys.boundingBox().let { box ->
        dijkstra(box.first, { it == box.second },
            distanceFn = { _, b -> points[b]!! },
            neighboursFn = { it.neighbours().filter { x -> points.containsKey(x) } }).getScore()!!
    }
}