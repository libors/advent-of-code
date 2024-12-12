package cz.libors.aoc.aoc21

import cz.libors.util.*

@Day("Smoke Basin")
object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input9.txt").toPointsWithValue().toMap()

        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Map<Point, Char>) = input
        .filter { it.key.neighbours().filter { p -> input.contains(p) }.all { p -> input[p]!! > it.value } }
        .map { it.value.toString().toInt() + 1 }
        .sum()

    private fun task2(input: Map<Point, Char>): Int {
        val valleys = mutableListOf<Int>()
        val remaining = input.filter { it.value != '9' }.keys.toMutableList()
        while (remaining.isNotEmpty()) {
            val valley = flood(remaining[0]) {
                it.neighbours().filter { p -> input.containsKey(p) && input[p] != '9' }
            }
            valleys.add(valley.size)
            remaining.removeAll(valley)
        }
        return valleys.sortedDescending().take(3).reduce { a, b -> a * b }
    }
}