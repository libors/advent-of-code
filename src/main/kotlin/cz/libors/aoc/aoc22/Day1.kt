package cz.libors.aoc.aoc22

import cz.libors.util.Day
import cz.libors.util.readToText
import cz.libors.util.splitByEmptyLine
import cz.libors.util.splitByNewLine

@Day("Calorie Counting")
object Day1 {

    private fun sumGroup(group: List<String>) = group.sumOf { num -> num.toInt() }
    private fun task1(data: List<List<String>>) = data.maxOfOrNull { sumGroup(it) }
    private fun task2(data: List<List<String>>) = data.map { sumGroup(it) }.sorted().takeLast(3).sum()

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input1.txt")
            .splitByEmptyLine()
            .map { it.splitByNewLine() }
        println(task1(input))
        println(task2(input))
    }
}