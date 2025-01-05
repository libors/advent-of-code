package cz.libors.aoc.aoc16

import cz.libors.util.Day
import cz.libors.util.readToText

@Day("Like a Rogue")
object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input18.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String) = countSafe(input, 40)
    private fun task2(input: String) = countSafe(input, 400000)

    private fun countSafe(input: String, rows: Int): Int {
        var x = input
        var sum = x.count { it == '.' }
        repeat(rows-1) {
            x =  derive(x)
            sum += x.count { it == '.' }
        }
        return sum
    }

    private fun derive(x: String): String {
        val result = StringBuilder()
        for (i in x.indices) {
            val l = if (i == 0) false else x[i - 1] == '^'
            val c = x[i] == '^'
            val r = if (i == x.length -1) false else x[i + 1] == '^'
            result.append(if (deriveTrap(l, c, r)) '^' else '.')
        }
        return result.toString()
    }

    private fun deriveTrap(l: Boolean, c: Boolean, r: Boolean): Boolean {
        if (l && c && !r) return true
        if (c && r && !l) return true
        if (l && !c && !r) return true
        if (r && !c && !l) return true
        return false
    }
}