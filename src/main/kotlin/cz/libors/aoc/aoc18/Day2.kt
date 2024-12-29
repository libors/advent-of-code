package cz.libors.aoc.aoc18

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Inventory Management System")
object Day2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input2.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task2(input: List<String>): String {
        for (i in 1 until input.size) {
            for (j in i until input.size) {
                if (isDiff1(input[i], input[j])) {
                    return input[i].zip(input[j]).filter { it.first == it.second }.map { it.first }.joinToString("")
                }
            }
        }
        throw IllegalArgumentException()
    }

    private fun task1(input: List<String>): Int {
        val freqs = input.map { line -> line.groupingBy { it }.eachCount() }
        return freqs.count { it.values.contains(2) } * freqs.count { it.values.contains(3) }
    }

    private fun isDiff1(x: String, y: String) = x.zip(y).filter { it.first != it.second }.size == 1
}