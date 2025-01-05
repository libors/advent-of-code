package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Doesn't He Have Intern-Elves For This?")
object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input5.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>) = input.count { isNice(it) }
    private fun task2(input: List<String>) = input.count { isReallyNice(it) }

    private fun isReallyNice(s: String) = Regex("(.).\\1").find(s) != null
            && Regex("(..).*\\1").find(s) != null

    private fun isNice(s: String) = s.count { "aeiou".contains(it) } >= 3
            && Regex("(.)\\1").find(s) != null
            && Regex("ab|cd|pq|xy").find(s) == null
}