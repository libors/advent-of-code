package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.Point
import cz.libors.util.Vector
import cz.libors.util.readToText

@Day("Perfectly Spherical Houses in a Vacuum")
object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input3.txt").map { Vector.from(it.toString())!! }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Vector>) = go(input, 1)
    private fun task2(input: List<Vector>) = go(input, 2)

    private fun go(input: List<Vector>, santas: Int): Int {
        val points = mutableSetOf<Point>()
        val positions = Array(santas) { Point(0, 0) }
        points.add(Point(0, 0))
        input.forEachIndexed { index, dir ->
            val idx = index % santas
            positions[idx] = positions[idx] + dir
            points.add(positions[idx])
        }
        return points.size
    }
}