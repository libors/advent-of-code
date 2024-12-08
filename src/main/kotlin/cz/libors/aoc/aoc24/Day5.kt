package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("Print Queue")
object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input5.txt").splitByEmptyLine()

        val ordering = input.first().splitByNewLine().map { it.findInts() }
        val pages = input.last().splitByNewLine().map { it.findInts() }

        println(task1(pages, ordering))
        println(task2(pages, ordering))
    }

    private fun task1(pages: List<List<Int>>, ordering: List<List<Int>>) = pages
        .filter { isInOrder(it, ordering) }
        .sumOf { it[it.size / 2] }

    private fun task2(pages: List<List<Int>>, ordering: List<List<Int>>) = pages
        .filter { !isInOrder(it, ordering) }
        .map { order(it, ordering) }
        .sumOf { it[it.size / 2] }

    private fun order(page: List<Int>, ordering: List<List<Int>>): List<Int> {
        var result = page.toMutableList()
        for (rule in ordering) {
            val i1 = result.indexOf(rule[0])
            val i2 = result.indexOf(rule[1])
            if (i1 != -1 && i2 != -1 && i1 > i2) {
                result.swap(i1, i2)
            }
        }
        if (!isInOrder(result, ordering)) {
            result = order(result, ordering) as MutableList
        }
        return result
    }

    private fun isInOrder(page: List<Int>, ordering: List<List<Int>>) = ordering.all {
        val i1 = page.indexOf(it[0])
        val i2 = page.indexOf(it[1])
        i1 == -1 || i2 == -1 || i1 < i2
    }
}