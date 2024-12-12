package cz.libors.aoc.aoc23

import cz.libors.util.*

@Day(name = "Gear Ratios")
object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input3.txt")
            .toPointsWithValue()
            .filter { p -> p.second != '.' }.toMap()

        println(task1(input))
        println(task2(input))
    }

    private fun Point.series(v: Vector, points: Set<Point>) = this.series(v) { points.contains(it) }

    private fun task2(input: Map<Point, Char>): Int {
        val symbols = input.filter { e -> e.value == '*' }.keys
        val digits = input.filter { e -> e.value.isDigit() }.keys
        return symbols.map { s ->
            s.neighbours(alsoDiag = true)
                .filter { digits.contains(it) }
                .map { it.series(Vector.LEFT, digits).last() }.toSet()
                .map { it.series(Vector.RIGHT, digits).map { d -> input[d]!! }.joinToString("").toInt() }
        }.filter { it.size == 2 }
            .sumOf { it.reduce { acc, i -> acc * i } }
    }

    private fun task1(input: Map<Point, Char>): Int {
        val symbols = input.filter { e -> !e.value.isDigit() }.keys
        val digits = input.filter { e -> e.value.isDigit() }.keys
        val touchingDigits = symbols.flatMap { s -> s.neighbours(alsoDiag = true).filter { digits.contains(it) } }
        val startDigits = touchingDigits.map { it.series(Vector.LEFT, digits).last() }.toSet()
        val fullNumbers =
            startDigits.map { it.series(Vector.RIGHT, digits).map { d -> input[d]!! }.joinToString("").toInt() }

        return fullNumbers.sum()
    }

}