package cz.libors.aoc.aoc24

import cz.libors.util.*

private typealias Area = Set<Point>

@Day("Garden Groups")
object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input12.txt").toPointsWithValue().toMap()
        val areas = splitToAreas(input)

        println(task1(areas))
        println(task2(areas))
    }

    private fun task1(areas: List<Area>) = areas.sumOf { it.size * perimeter(it) }
    private fun task2(areas: List<Area>) = areas.sumOf { it.size * sides(it) }

    private fun perimeter(area: Area) = area.sumOf { it.neighbours().count { n -> !area.contains(n) } }

    private fun sides(area: Area): Int {
        val cols = area.groupBy { it.x }.values.toList()
        val rows = area.groupBy { it.y }.values.toList()
        return countEdges(cols, area, Vector.LEFT) +
                countEdges(cols, area, Vector.RIGHT) +
                countEdges(rows, area, Vector.UP) +
                countEdges(rows, area, Vector.DOWN)
    }

    private fun countEdges(stripes: List<List<Point>>, all: Area, edgeDir: Vector) = stripes
        .map { findEdgePoints(it, all, edgeDir) }
        .sumOf { separateCount(it) }

    private fun findEdgePoints(stripe: List<Point>, all: Area, edgeDir: Vector) = stripe.filter { !all.contains(it + edgeDir) }.toSet()

    private fun splitToAreas(input: Map<Point, Char>) = dividePoints(input.keys) { x, y -> input[x] == input[y] }

    private fun separateCount(area: Area) = dividePoints(area) { _, y -> area.contains(y)}.count()
}