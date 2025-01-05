package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.readToText

@Day("Elves Look, Elves Say")
object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input10.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String) = process(input, 40)
    private fun task2(input: String) = process(input, 50)

    private fun process(input: String, num: Int): Int {
        var x = input
        repeat(num) { x = process(x) }
        return x.length
    }

    private fun process(s: String): String {
        val result = StringBuilder()
        var inRow = 0
        var c = '@'
        for (i in s.indices) {
            if (inRow == 0) {
                c = s[i]
                inRow++
            } else if (c == s[i]) {
                inRow++
            } else {
                result.append(inRow).append(c)
                inRow = 1
                c = s[i]
            }
        }
        result.append(inRow).append(c)
        return result.toString()
    }
}