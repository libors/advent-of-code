package cz.libors.aoc.aoc22

import cz.libors.util.*

@Day(name = "Boiling Boulders")
object Day18 {

    private fun task1(points: Set<Point3>) = points
        .flatMap { it.adjacent() }
        .count { !points.contains(it) }

    private fun task2(points: Set<Point3>): Int {
        val (min, max) = points.boundingBox3().let {
            Pair(it.first + Vector3(-1, -1, -1), it.second + Vector3(1, 1, 1))
        }
        val air = flood(min) { it.adjacent().filter { x -> x.inRect(min, max) && !points.contains(x) } }
        return points.sumOf { it.adjacent().count { a -> air.contains(a) } }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val points = readToLines("input18.txt")
            .map { it.findInts() }
            .map { Point3(it[0], it[1], it[2]) }
            .toSet()
        println(task1(points))
        println(task2(points))
    }
}