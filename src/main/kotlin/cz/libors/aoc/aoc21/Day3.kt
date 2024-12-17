package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Binary Diagnostic")
object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input3.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>): Int {
        var x = ""
        var y = ""
        for (i in 0 until input[0].length) {
            val c = common(input, i, true)
            x += c
            y += switch(c)
        }
        return x.toInt(2) * y.toInt(2)
    }

    private fun task2(input: List<String>): Int {
        var inp = input
        var i = 0
        while (inp.size > 1) {
            inp = inp.filter { it[i] == common(inp, i, true) }
            i++
        }
        val a = inp[0]
        inp = input
        i = 0
        while (inp.size > 1) {
            inp = inp.filter { it[i] == common(inp, i, false) }
            i++
        }
        val b = inp[0]
        return a.toInt(2) * b.toInt(2)
    }

    private fun switch(x: Char) = if (x == '0') '1' else '0'
    private fun common(input: List<String>, i: Int, most: Boolean): Char {
        val max = input.map { it[i] }.groupBy { it }.toList()
        if (max[0].second.size == max[1].second.size) return if (most) '1' else '0'
        return if (most) max.maxByOrNull { it.second.size }!!.first else max.minByOrNull { it.second.size }!!.first
    }
}