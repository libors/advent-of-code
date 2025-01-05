package cz.libors.aoc.aoc16

import cz.libors.util.Day
import cz.libors.util.readToText
import kotlin.time.measureTime

@Day("Dragon Checksum")
object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input16.txt")
        println(task1(input))
        measureTime { println(task2(input)) }.also { println(it) }
    }

    private fun task1(input: String) = checksum(fill(input, 272))
    private fun task2(input: String) = checksum(fill(input, 35651584))

    private fun fill(input: String, size: Int): String {
        var s = input
        while (s.length < size) {
            val sb = StringBuilder(s)
            sb.append('0')
            for (i in s.length - 1 downTo 0) sb.append(if (s[i] == '0') '1' else '0')
            s = sb.toString()
        }
        return s.substring(0, size)
    }

    private fun checksum(s: String): String {
        if (s.length % 2 == 1) return s
        val result = StringBuilder()
        for (i in s.indices step 2) {
            if (s[i] == s[i + 1]) result.append('1') else result.append('0')
        }
        return checksum(result.toString())
    }
}