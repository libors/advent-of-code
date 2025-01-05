package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.readToText

@Day("Not Quite Lisp")
object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input1.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task2(input: String): Int {
        var floor = 0
        var i = 0
        while (floor >= 0) {
            val ch = input[i++]
            if (ch == '(') floor++ else floor--
        }
        return i
    }

    private fun task1(input: String) = input.count { it == '(' } - input.count { it == ')' }
}