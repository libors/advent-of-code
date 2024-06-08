package cz.libors.aoc.aoc23

import cz.libors.util.*
import kotlin.math.abs
import kotlin.math.min

@Day(name = "Point of Incidence")
object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input13.txt").splitByEmptyLine().map { maze ->
            maze.splitByNewLine().toPointsWithValue().filter { it.second == '#' }.map { it.first }
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<List<Point>>) = input.sumOf { findMirrors(it, false) }
    private fun task2(input: List<List<Point>>) = input.sumOf { findMirrors(it, true) }

    private fun findMirrors(points: List<Point>, withSmudge: Boolean): Int {
        val xMirror = findXReflection(points, withSmudge)
        if (xMirror >= 0) return xMirror

        val yMirror = findXReflection(points.map { Point(it.y, it.x) }, withSmudge) // find on transposed data
        if (yMirror > 0) return yMirror * 100

        throw IllegalArgumentException("No reflection found")
    }

    private fun findXReflection(points: List<Point>, withSmudge: Boolean): Int {
        val box = points.boundingBox()
        for (c in box.first.x until box.second.x) {
            if (checkReflection(c, box, points, withSmudge)) return c - box.first.x + 1
        }
        return -1
    }

    private fun checkReflection(c: Int, box: Pair<Point, Point>, points: List<Point>, withSmudge: Boolean): Boolean {
        val width = min(c + 1 - box.first.x, box.second.x - c)
        val left = points.filter { it.x <= c && it.x > c - width }
        val right = points.filter { it.x > c && it.x <= c + width }

        if ((!withSmudge && left.size == right.size) || (withSmudge && abs(left.size - right.size) == 1)) {
            val reflection = left.map { Point(c + (c - it.x + 1), it.y) }
            return if (withSmudge) {
                if (left.size < right.size) right.containsAll(reflection)
                else reflection.count { right.contains(it) } == right.size
            } else right.containsAll(reflection)
        }
        return false
    }
}