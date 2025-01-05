package cz.libors.aoc.aoc16

import cz.libors.util.Day
import cz.libors.util.md5
import cz.libors.util.readToText

@Day("How About a Nice Game of Chess?")
object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input5.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String): String {
        val result = StringBuilder()
        var i = 0
        while (result.length < 8) {
            val hash = md5(input + i++)
            if (hash.startsWith("00000")) result.append(hash[5])
        }
        return result.toString()
    }

    private fun task2(input: String): String {
        val result = "xxxxxxxx".toCharArray()
        var i = 0
        var found = 0
        while (found < 8) {
            val hash = md5(input + i++)
            if (hash.startsWith("00000")) {
                val position = hash[5]
                if (position.isDigit() && position.digitToInt() < 8 && result[position.digitToInt()] == 'x') {
                    result[position.digitToInt()] = hash[6]
                    found++
                }
            }
        }
        return String(result)
    }
}