package cz.libors.aoc.aoc20

import cz.libors.util.Day
import cz.libors.util.readToText
import cz.libors.util.splitByEmptyLine
import cz.libors.util.splitByNewLine

@Day("Custom Customs")
object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input6.txt").splitByEmptyLine().map { it.splitByNewLine() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<List<String>>) = input.sumOf { it.joinToString("").toCharArray().distinct().size }

    private fun task2(input: List<List<String>>) = input.sumOf { group ->
        group.joinToString("").groupBy { it }.count { it.value.size == group.size }
    }
}