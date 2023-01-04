package cz.libors.aoc.aoc22

import cz.libors.util.readToText
import cz.libors.util.splitByEmptyLine
import cz.libors.util.splitByNewLine
import org.json.simple.parser.JSONParser
import java.lang.Integer.min

object Day13 {

    private fun task1(data: List<List<List<*>>>) =
        data.mapIndexed { idx, it -> Pair(idx + 1, compare(it[0], it[1])) }
            .filter { it.second < 0 }
            .sumOf { it.first }

    private fun task2(data: List<List<List<*>>>): Int {
        val d1 = listOf(listOf(2L))
        val d2 = listOf(listOf(6L))
        val sorted = data.flatten()
            .plus(listOf(d1, d2))
            .sortedWith { l, r -> compare(l, r).toInt() }
        return (sorted.indexOf(d1) + 1) * (sorted.indexOf(d2) + 1)
    }

    private fun compare(first: Any, second: Any): Long {
        if (first is Long && second is Long)
            return first - second
        else if (first is Long)
            return compare(listOf(first), second)
        else if (second is Long)
            return compare(first, listOf(second))
        else {
            val left = first as List<*>
            val right = second as List<*>
            for (i in 0 until min(left.size, right.size)) {
                val cmp = compare(left[i]!!, right[i]!!)
                if (cmp != 0L) return cmp
            }
            return (left.size - right.size).toLong()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input13.txt")
            .splitByEmptyLine()
            .map { it.splitByNewLine().map { s -> JSONParser().parse(s) as List<*> } }
        println(task1(input))
        println(task2(input))
    }
}