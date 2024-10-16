package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.readToLines
import java.util.LinkedList

@Day("Syntax Scoring")
object Day10 {

    private val open = "([{<"
    private val close = ")]}>"
    private val errorScoreMap = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
    private val autocompleteScoreMap = mapOf('(' to 1, '[' to 2, '{' to 3, '<' to 4)

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input10.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>): Int {
        var score = 0
        for (line in input) {
            val stack = LinkedList<Char>()
            for (i in line.indices) {
                val ch = line[i]
                if (open.contains(ch)) {
                    stack.push(ch)
                } else {
                    val other = stack.pop()
                    if (close.indexOf(ch) != open.indexOf(other)) {
                        score += errorScoreMap[ch]!!
                        break
                    }
                }
            }
        }
        return score
    }

    private fun task2(input: List<String>): Long {
        val scores = mutableListOf<Long>()
        var error: Boolean
        for (line in input) {
            error = false
            val stack = LinkedList<Char>()
            for (i in line.indices) {
                val ch = line[i]
                if (open.contains(ch)) {
                    stack.push(ch)
                } else {
                    val other = stack.pop()
                    if (close.indexOf(ch) != open.indexOf(other)) {
                        error = true
                        break
                    }
                }
            }
            if (!error && stack.isNotEmpty())
                scores.add(stack.fold(0L) { acc, ch -> acc * 5 + autocompleteScoreMap[ch]!! })
        }
        return scores.sorted()[scores.size / 2]
    }
}