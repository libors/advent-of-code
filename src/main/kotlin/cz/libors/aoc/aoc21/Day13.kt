package cz.libors.aoc.aoc21

import cz.libors.util.*

@Day("Transparent Origami")
object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input13.txt").splitByEmptyLine()
        val points = input[0].lines()
            .map { it.findInts() }
            .map { Point(it[0], it[1]) }
            .toSet()
        val instructions = input[1].lines()
            .map { it.substringAfter("along ").split("=") }
            .map { Fold(it[0][0], it[1].toInt()) }

        println(task1(points, instructions))
        println(PlanePrinter(charRepresentation = mapOf(0L to ' ', 1L to '#')).print(task2(points, instructions).associateWith { 1 }))
    }

    private fun task1(points: Set<Point>, instructions: List<Fold>) = fold(points, instructions[0]).size
    private fun task2(points: Set<Point>, instructions: List<Fold>) = instructions.fold(points) { acc, i -> fold(acc, i) }

    private fun fold(points: Set<Point>, where: Fold): Set<Point> = when (where.axis) {
        'x' -> points.mapNotNull {
            if (it.x == where.num)
                null
            else
                Point(if (it.x < where.num) it.x else where.num - (it.x - where.num), it.y)
        }
        else -> points.mapNotNull {
            if (it.y == where.num)
                null
            else
                Point(it.x, if (it.y < where.num) it.y else where.num - (it.y - where.num))
        }
    }.toSet()

    private data class Fold(val axis: Char, val num: Int)
}