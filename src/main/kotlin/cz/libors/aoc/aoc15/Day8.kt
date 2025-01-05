package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Matchsticks")
object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input8.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>) = input.sumOf { it.length - unescape(it).length }
    private fun task2(input: List<String>) = input.sumOf { escapeChars(it) }

    private fun unescape(s: String): String {
        var result = s.substring(1, s.length - 1)
        result = result.replace("\\\"", "\"").replace("\\\\", "\\")
        result = result.replace(Regex("\\\\x[0-9abcdef]{2}"), "@")
        return result
    }

    private fun escapeChars(s: String) = s.count { it == '\\' || it == '"' } + 2
}