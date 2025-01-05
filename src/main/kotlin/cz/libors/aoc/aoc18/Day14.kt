package cz.libors.aoc.aoc18

import cz.libors.util.Day
import cz.libors.util.readToText

@Day("Chocolate Charts")
object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input14.txt").toInt()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Int): String {
        val list = mutableListOf(3, 7)
        var pos1 = 0
        var pos2 = 1
        while (list.size < input + 10) {
            val sumStr = (list[pos1] + list[pos2]).toString()
            for (ch in sumStr) list.add(ch.digitToInt())
            pos1 = (pos1 + list[pos1] + 1) % list.size
            pos2 = (pos2 + list[pos2] + 1) % list.size
        }
        return (input until input + 10).map { list[it] }.joinToString("")
    }

    private fun task2(input: Int): Int {
        val list = mutableListOf(3, 7)
        var pos1 = 0
        var pos2 = 1
        val expected = input.toString().map { it.digitToInt() }
        while (indexOf(expected, list) == -1) {
            val sumStr = (list[pos1] + list[pos2]).toString()
            for (ch in sumStr) list.add(ch.digitToInt())
            pos1 = (pos1 + list[pos1] + 1) % list.size
            pos2 = (pos2 + list[pos2] + 1) % list.size
        }
        return indexOf(expected, list)
    }

    private fun indexOf(expected: List<Int>, list: List<Int>): Int {
        if (list.size < expected.size + 1) return -1
        var ok = true
        for (i in expected.indices) {
            if (expected[i] != list[list.size - expected.size + i]) {
                ok = false
                break
            }
        }
        if (ok) return list.size - expected.size
        ok = true
        for (i in expected.indices) {
            if (expected[i] != list[list.size - expected.size + i - 1]) {
                ok = false
                break
            }
        }
        if (ok) return list.size - expected.size - 1
        return -1
    }
}