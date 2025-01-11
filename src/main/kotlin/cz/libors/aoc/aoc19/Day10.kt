package cz.libors.aoc.aoc19

import cz.libors.util.*
import kotlin.math.atan2

@Day("Monitoring Station")
object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input10.txt").toPointsWithValue()
            .filter { it.second == '#' }.map { it.first }
        val monitor = task1(input)
        println(monitor.second) // 247
        println(task2(input, monitor.first)) // 1919
    }

    private fun task1(asteroidMap: List<Point>) = asteroidMap
        .map { Pair(it, countVisible(it, asteroidMap)) }
        .maxBy { it.second }

    private fun countVisible(start: Point, asteroidMap: List<Point>) = asteroidMap
        .map { start.vectorTo(it).normalize() }
        .toSet().size - 1

    private fun task2(asteroidMap: List<Point>, center: Point): Int {
        val directionSorted = asteroidMap
            .filter { it != center }
            .groupBy { center.vectorTo(it).normalize() }
            .mapKeys { degree12h(it.key) }
            .toSortedMap()
            .mapValues {
                it.value.sortedBy { asteroid ->
                    center.vectorTo(asteroid).let { p -> p.x * p.y + p.y * p.y }
                }
            }

        val laserTargets = laserShotSort(directionSorted.values.toList())
        return laserTargets[199].let { it.x * 100 + it.y }
    }

    private fun degree12h(vector: Vector) = (vector.degreesPositive() + 90) % 360

    private fun laserShotSort(list: List<List<Point>>): List<Point> {
        val size = list.size
        val subSize = list.maxOf { it.size }
        val result = mutableListOf<Point>()
        var i = 0
        var j = 0
        for (n in 1..size * subSize) {
            if (list[i].size > j) result.add(list[i][j])
            i++
            if (i == size) {
                i = 0
                j++
            }
        }
        return result
    }

    private fun Vector.degreesPositive(): Double {
        val angle = atan2(y.toDouble(), x.toDouble()) * 180 / Math.PI
        return if (angle >= 0) angle else angle + 360
    }
}