package cz.libors.aoc.aoc16

import cz.libors.util.*

@Day("Two-Factor Authentication")
object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input8.txt")
        val points = movePoints(input)
        println(task1(points))
        println(task2(points))
    }

    private fun task1(points: Set<Point>) = points.size
    private fun task2(points: Set<Point>) = PlanePrinter(charRepresentation = mapOf(0L to ' ', 1L to '#'))
        .print(points.associateWith { 1 })

    private fun movePoints(input: List<String>): Set<Point> {
        val points = mutableSetOf<Point>()
        val xSize = 50
        val ySize = 6
        for (i in input) {
            val ints = i.findInts()
            if (i.startsWith("rect")) {
                for (x in 0 until ints[0])
                    for (y in 0 until ints[1])
                        points.add(Point(x, y))
            } else if (i.startsWith("rotate row")) {
                val (y, moveBy) = ints
                val origPoints = points.filter { it.y == y }
                points.removeIf { it.y == y }
                origPoints.forEach { points.add(Point((it.x + moveBy) % xSize, it.y)) }
            } else {
                val (x, moveBy) = ints
                val origPoints = points.filter { it.x == x }
                points.removeIf { it.x == x }
                origPoints.forEach { points.add(Point(it.x, (it.y + moveBy) % ySize)) }
            }
        }
        return points
    }
}