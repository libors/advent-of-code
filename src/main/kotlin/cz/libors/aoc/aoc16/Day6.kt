package cz.libors.aoc.aoc16

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Signals and Noise")
object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input6.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>) = decrypt(input, ::mostFrequentChar)
    private fun task2(input: List<String>) = decrypt(input, ::leastFrequentChar)

    private fun decrypt(input: List<String>, fn: (List<Pair<Int, Char>>) -> Char) = input
        .flatMap { it.mapIndexed { idx, ch -> Pair(idx, ch) } }
        .groupBy { it.first }
        .mapValues { fn(it.value) }
        .toList().sortedBy { it.first }
        .map { it.second }.joinToString("")

    private fun mostFrequentChar(list: List<Pair<Int, Char>>): Char = list
        .map { it.second }.groupingBy { it }.eachCount()
        .toList().maxByOrNull { it.second }!!.first

    private fun leastFrequentChar(list: List<Pair<Int, Char>>): Char = list
        .map { it.second }.groupingBy { it }.eachCount()
        .toList().minByOrNull { it.second }!!.first
}