package cz.libors.aoc.aoc19

import cz.libors.util.*
import cz.libors.util.Vector
import java.lang.RuntimeException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.pow

@Day("Planet of Discord")
object Day24 {

    private const val bug = '#'
    private const val empty = '.'

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input24.txt").toPointsWithValue().toMap()
        println(task1(input))
        println(task2(input, 200))
    }

    private fun task1(input: Map<Point, Char>): Int {
        val visited = HashSet<Int>()
        visited.add(computeBiodiversity(input))
        var plane = input
        do {
            plane = update(plane)
            val firstIncidence = visited.add(computeBiodiversity(plane))
        } while (firstIncidence)
        return computeBiodiversity(plane)
    }

    private fun task2(plane: Map<Point, Char>, iterations: Int): Int {
        val initPoints = plane.mapKeys { PointOnLevel(iterations, it.key) }.toMutableMap()
        for (level in 0 until iterations)
            emptyPlane(level).forEach { initPoints[it] = empty }
        for (level in iterations + 1..iterations * 2)
            emptyPlane(level).forEach { initPoints[it] = empty }

        var points = initPoints.toMap()
        for (i in 1..iterations)
            points = update(points, iterations * 2)

        return points.values.count { it == bug }
    }

    private fun emptyPlane(level: Int): List<PointOnLevel> {
        val result = ArrayList<PointOnLevel>()
        for (i in 0 until 5)
            for (j in 0 until 5)
                result.add(PointOnLevel(level, Point(i, j)))
        return result
    }

    private fun computeBiodiversity(plane: Map<Point, Char>) = plane.filterValues { it == bug }
        .map { 2.toDouble().pow(it.key.y * 5 + it.key.x) }.sum().toInt()

    private fun update(plane: Map<Point, Char>): Map<Point, Char> {
        val result = HashMap<Point, Char>()
        for (x in 0 until 5)
            for (y in 0 until 5) {
                val p = Point(x, y)
                result[p] = updatedValue(plane[p]!!, p.neighbours().map { plane[it] ?: empty })
            }
        return result
    }

    private fun update(points: Map<PointOnLevel, Char>, maxLevel: Int): Map<PointOnLevel, Char> {
        val result = HashMap<PointOnLevel, Char>()
        for (level in 1 until maxLevel) {
            for (x in 0 until 5)
                for (y in 0 until 5) {
                    if (x != 2 || y != 2) {
                        val p = PointOnLevel(level, Point(x, y))
                        result[p] = updatedValue(points[p] ?: throw UnknownPoint(p),
                            adjacentPoints(p).map { points[it] ?: throw UnknownPoint(it) })
                    }
                }
        }
        emptyPlane(0).forEach { result[it] = empty }
        emptyPlane(maxLevel).forEach { result[it] = empty }
        return result
    }

    private fun adjacentPoints(p: PointOnLevel): List<PointOnLevel> {
        val level = p.level
        return when (val x = p.point) {
            Point(1, 1), Point(1, 3), Point(3, 1), Point(3, 3) -> x.neighbours().map { PointOnLevel(level, it) }
            Point(1, 0), Point(2, 0), Point(3, 0) -> listOf(
                PointOnLevel(level, x.add(Vector.LEFT)),
                PointOnLevel(level, x.add(Vector.DOWN)),
                PointOnLevel(level, x.add(Vector.RIGHT)),
                PointOnLevel(level + 1, Point(2, 1))
            )
            Point(1, 4), Point(2, 4), Point(3, 4) -> listOf(
                PointOnLevel(level, x.add(Vector.LEFT)),
                PointOnLevel(level, x.add(Vector.UP)),
                PointOnLevel(level, x.add(Vector.RIGHT)),
                PointOnLevel(level + 1, Point(2, 3))
            )
            Point(0, 1), Point(0, 2), Point(0, 3) -> listOf(
                PointOnLevel(level, x.add(Vector.DOWN)),
                PointOnLevel(level, x.add(Vector.UP)),
                PointOnLevel(level, x.add(Vector.RIGHT)),
                PointOnLevel(level + 1, Point(1, 2))
            )
            Point(4, 1), Point(4, 2), Point(4, 3) -> listOf(
                PointOnLevel(level, x.add(Vector.DOWN)),
                PointOnLevel(level, x.add(Vector.UP)),
                PointOnLevel(level, x.add(Vector.LEFT)),
                PointOnLevel(level + 1, Point(3, 2))
            )
            Point(0, 0) -> listOf(
                PointOnLevel(level, x.add(Vector.DOWN)),
                PointOnLevel(level, x.add(Vector.RIGHT)),
                PointOnLevel(level + 1, Point(2, 1)),
                PointOnLevel(level + 1, Point(1, 2))
            )
            Point(4, 0) -> listOf(
                PointOnLevel(level, x.add(Vector.DOWN)),
                PointOnLevel(level, x.add(Vector.LEFT)),
                PointOnLevel(level + 1, Point(2, 1)),
                PointOnLevel(level + 1, Point(3, 2))
            )
            Point(0, 4) -> listOf(
                PointOnLevel(level, x.add(Vector.UP)),
                PointOnLevel(level, x.add(Vector.RIGHT)),
                PointOnLevel(level + 1, Point(1, 2)),
                PointOnLevel(level + 1, Point(2, 3))
            )
            Point(4, 4) -> listOf(
                PointOnLevel(level, x.add(Vector.UP)),
                PointOnLevel(level, x.add(Vector.LEFT)),
                PointOnLevel(level + 1, Point(3, 2)),
                PointOnLevel(level + 1, Point(2, 3))
            )
            Point(2, 1) -> listOf(
                PointOnLevel(level, x.add(Vector.UP)),
                PointOnLevel(level, x.add(Vector.LEFT)),
                PointOnLevel(level, x.add(Vector.RIGHT))
            ) + (0 until 5).map { PointOnLevel(level - 1, Point(it, 0)) }
            Point(1, 2) -> listOf(
                PointOnLevel(level, x.add(Vector.UP)),
                PointOnLevel(level, x.add(Vector.LEFT)),
                PointOnLevel(level, x.add(Vector.DOWN))
            ) + (0 until 5).map { PointOnLevel(level - 1, Point(0, it)) }
            Point(3, 2) -> listOf(
                PointOnLevel(level, x.add(Vector.UP)),
                PointOnLevel(level, x.add(Vector.RIGHT)),
                PointOnLevel(level, x.add(Vector.DOWN))
            ) + (0 until 5).map { PointOnLevel(level - 1, Point(4, it)) }
            Point(2, 3) -> listOf(
                PointOnLevel(level, x.add(Vector.LEFT)),
                PointOnLevel(level, x.add(Vector.RIGHT)),
                PointOnLevel(level, x.add(Vector.DOWN))
            ) + (0 until 5).map { PointOnLevel(level - 1, Point(it, 4)) }
            else -> throw RuntimeException("Unhandled point: $x")
        }
    }

    private fun updatedValue(value: Char, adjacentValues: List<Char>) =
        if (value == bug) {
            if (adjacentValues.count { it == bug } == 1) bug else empty
        } else {
            if (adjacentValues.count { it == bug } in (1..2)) bug else empty
        }

    private data class PointOnLevel(val level: Int, val point: Point)

    private class UnknownPoint(p: PointOnLevel) : RuntimeException("Unknown point: $p")
}