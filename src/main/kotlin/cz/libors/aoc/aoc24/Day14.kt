package cz.libors.aoc.aoc24

import cz.libors.util.*
import java.awt.Color

object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input14.txt").map { it.findInts() }.map {
            Robot(Point(it[0], it[1]), Vector(it[2], it[3]))
        }
        val box = Point(101, 103)
        println(task1(input, box))
        println(task2(input, box))
    }

    private fun task1(robots: List<Robot>, box: Point): Int {
        var r = robots
        repeat(100) { r = r.map { it.update(box) } }
        return score(r.map { it.pos }, Point(box.x / 2, box.y / 2))
    }

    private fun task2(robots: List<Robot>, box: Point): Int {
        var r = robots
        var second = 0
        while (!isTree(r.map { it.pos })) {
            r = r.map { it.update(box) }
            second++
        }
        // Graphics(colorSchema = ColorSchemas.staticColors(listOf(Color.GREEN))).showPoints(r.map { it.pos })
        return second
    }

    private fun score(robots: List<Point>, center: Point): Int {
        val q1 = robots.count { it.x < center.x && it.y < center.y }
        val q2 = robots.count { it.x > center.x && it.y < center.y }
        val q3 = robots.count { it.x < center.x && it.y > center.y }
        val q4 = robots.count { it.x > center.x && it.y > center.y }
        return q1 * q2 * q3 * q4
    }

    private fun isTree(robots: List<Point>) = robots.groupBy { it.x }.values.count { containsLine(it) } >= 3

    private fun containsLine(it: List<Point>): Boolean {
        val yList = it.map { it.y }.sorted()
        var conseq = 0
        var last = -10
        for (y in yList) {
            if (y == last + 1) {
                if (++conseq == 10) return true
            } else {
                conseq = 1
            }
            last = y
        }
        return false
    }

    private data class Robot(val pos: Point, val dir: Vector) {
        fun update(box: Point) = Robot(Point((pos.x + dir.x).posMod(box.x), (pos.y + dir.y).posMod(box.y)), dir)
    }
}