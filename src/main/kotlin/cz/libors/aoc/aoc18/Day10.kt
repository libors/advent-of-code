package cz.libors.aoc.aoc18

import cz.libors.util.*

@Day("The Stars Align")
object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input10.txt")
            .map { line -> line.findInts().let { Star(Point(it[0], it[1]), Vector(it[2], it[3])) } }
        val result = task1And2(input)
        println(PlanePrinter(charRepresentation = mapOf(0L to ' ', 1L to '#')).print(result.first.associateWith { 1 }))
        println(result.second)
    }

    private fun task1And2(input: List<Star>):Pair<List<Point>, Int> {
        var i = 0
        while (true) {
            i++
            input.forEach { it.move() }
            val positions = input.map { it.pos }
            val size = positions.boundingBox().size()
            if (size.first < 100 && size.second < 100 && containsLetters(positions)) {
                return Pair(positions, i)
            }
        }
    }

    private data class Star(var pos: Point, val v: Vector) {
        fun move() {
            pos += v
        }
    }

    private fun containsLetters(positions: List<Point>) = positions.groupBy { it.x }.values.count { containsVerticalLine(it) } >= 3

    private fun containsVerticalLine(points: List<Point>): Boolean {
        val coords = points.map { it.y }.sorted()
        var seq = 0
        var prev = Int.MIN_VALUE
        for (y in coords) {
            if (y == prev) continue
            if (y == prev + 1) seq++ else seq = 1
            if (seq > 5) return true
            prev = y
        }
        return false
    }
}