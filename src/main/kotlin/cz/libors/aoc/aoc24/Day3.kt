package cz.libors.aoc.aoc24

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToText

@Day("Mull It Over")
object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input3.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String): Int {
        val regex = Regex("mul\\([0-9]{1,3},[0-9]{1,3}\\)")
        return regex.findAll(input)
            .map { match -> with(match.value.findInts()) { this[0] * this[1] } }
            .sum()
    }

    private fun task2(input: String): Int {
        val regex = Regex("(mul\\([0-9]{1,3},[0-9]{1,3}\\)|do\\(\\)|don't\\(\\))")
        var result = 0
        var enabled = true
        regex.findAll(input).forEach {
            when (it.value) {
                "do()" -> enabled = true
                "don't()" -> enabled = false
                else -> if (enabled) with(it.value.findInts()) { result += this[0] * this[1] }
            }
        }
        return result
    }
}