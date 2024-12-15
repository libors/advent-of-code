package cz.libors.aoc.aoc24

import cz.libors.util.*
import kotlin.math.max

object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input14.txt").map { it.findInts() }
            .map { Robot(Point(it[0], it[1]), Vector(it[2], it[3])) }
        val box = Point(101, 103)
        println(task1(input, box))
        println(task2(input, box))
    }

    private fun task1(robots: List<Robot>, box: Point): Int {
        var r = robots
        repeat(100) { r = r.map { it.update(box) } }
        return quadrantScore(r.map { it.pos }, Point(box.x / 2, box.y / 2))
    }

    private fun task2(robots: List<Robot>, box: Point): Int {
        var r = robots
        var second = 0
        while (!containsTree(r.map { it.pos })) {
            r = r.map { it.update(box) }
            second++
        }
        //Graphics().showInts(r.map { it.pos }.associateWith { 1 })
        return second
    }

    private fun quadrantScore(robots: List<Point>, center: Point) = robots
        .filter { it.x != center.x && it.y != center.y }
        .groupingBy { Pair(it.x < center.x, it.y < center.y) }.eachCount().values
        .fold(1) { a, b -> a * b }

    private fun containsTree(positions: List<Point>) = positions.groupBy { it.x }.values.count { containsLine(it) } >= 3

    private fun containsLine(it: List<Point>) = it.map { it.y }.sorted()
        .zipWithNext()
        .fold(Pair(0, 0)) { (cur, max), x ->
            if (x.second == x.first + 1) Pair(cur + 1, max(max, cur + 1)) else Pair(0, max)
        }.second > 10

    private data class Robot(val pos: Point, val dir: Vector) {
        fun update(box: Point) = Robot(Point((pos.x + dir.x).posMod(box.x), (pos.y + dir.y).posMod(box.y)), dir)
    }
}