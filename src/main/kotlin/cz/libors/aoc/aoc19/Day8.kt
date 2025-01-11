package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.readToText

@Day("pace Image Format")
object Day8 {
    private const val LAYER_SIZE = 25 * 6

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input8.txt").toCharArray().map(Character::getNumericValue)
        println(task1(input))
        println(task2(input))
    }

    private fun task1(values: List<Int>): Int {
        val fewestZeroesLayer = layerStats(values).withIndex().minBy { (_, layer) -> layer[0] ?: 0 }.value
        return (fewestZeroesLayer[1] ?: 0) * (fewestZeroesLayer[2] ?: 0)
    }

    private fun task2(values: List<Int>): String {
        val pixels = values.chunked(LAYER_SIZE).reduce(::combineLayers).map { if (it == 0) ' ' else '#' }
        return pixels.chunked(25).joinToString("\n") { row -> row.joinToString("") }
    }

    private fun layerStats(values: List<Int>): List<Map<Int, Int>> = values.chunked(LAYER_SIZE)
        .map { list -> list.groupingBy { it }.eachCount() }


    private fun combineLayers(l1: List<Int>, l2: List<Int>): List<Int> = l1.zip(l2)
        .map { if (it.first == 2) it.second else it.first }
}