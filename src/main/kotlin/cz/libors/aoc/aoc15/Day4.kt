package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.md5
import cz.libors.util.readToText

@Day("The Ideal Stocking Stuffer")
object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input4.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String) = findNum(input, "00000")
    private fun task2(input: String) = findNum(input, "000000")

    private fun findNum(input: String, prefix: String): Int {
        var i = 0
        while (true) {
            if (md5(input + i.toString()).startsWith(prefix)) return i
            i++
        }
    }
}