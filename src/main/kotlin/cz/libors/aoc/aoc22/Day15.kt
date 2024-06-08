@file:Suppress("SameParameterValue")

package cz.libors.aoc.aoc22

import cz.libors.util.*
import kotlin.math.abs
import kotlin.math.max

@Day(name = "Beacon Exclusion Zone")
object Day15 {

    private fun task1(sensors: List<Sensor>, occupied: Set<Point>, row: Int): Int {
        var count = 0
        val fromRange = occupied.minOf { it.x } - sensors.maxOf { it.reach }
        val toRange = occupied.maxOf { it.x } + sensors.maxOf { it.reach }
        for (i in fromRange..toRange) {
            val p = Point(i, row)
            if (!occupied.contains(p) && sensors.any { it.inReach(p) })
                count++
        }
        return count
    }

    private fun task2(sensors: List<Sensor>, maxBound: Long): Long {
        for (y in 0..maxBound) {
            val coveredRanges = sensors.map { it.xRange(y) }.filter { !it.isEmpty() }
            val hole = findHole(Interval(0, maxBound), coveredRanges)
            if (hole != null) {
                return hole * 4_000_000L + y
            }
        }
        throw RuntimeException()
    }

    private fun findHole(interval: Interval, ranges: List<Interval>): Long? {
        val sorted = ranges.sortedBy { it.from }
        if (sorted[0].from > interval.from)
            return interval.from
        var max = interval.from
        for (r in sorted) {
            if (r.from > max + 1) return max + 1
            max = max(max, r.to)
        }
        if (max < interval.to) return max + 1
        return null
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val coords = readToLines("input15.txt")
            .map { it.findInts() }

        val sensors = coords.map { Point(it[0], it[1]). let { s -> Sensor(s, s.manhattanDistance(Point(it[2], it[3]))) } }
        val occupied = mutableSetOf<Point>()
        coords.forEach {
            occupied.add(Point(it[0], it[1]))
            occupied.add(Point(it[2], it[3]))
        }

        println(task1(sensors, occupied, 2_000_000))
        println(task2(sensors, 4_000_000))
    }

    data class Sensor(val p: Point, val reach: Int) {
        fun inReach(other: Point) = p.manhattanDistance(other) <= reach
        fun xRange(y: Long): Interval {
            val yReach = reach - abs(y - this.p.y)
            return if (yReach < 0) Interval.EMPTY else Interval(p.x - yReach, p.x + yReach)
        }
    }
}