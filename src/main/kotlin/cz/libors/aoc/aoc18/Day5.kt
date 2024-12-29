package cz.libors.aoc.aoc18

import cz.libors.util.Day
import cz.libors.util.readToText
import java.util.*

@Day("Alchemical Reduction")
object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input5.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String) = reduce(input)

    private fun task2(input: String) = input.lowercase().toList().distinct()
        .minOf { reduce(input.replace(it.toString(), "", ignoreCase = true)) }

    private fun reduce(input: String): Int {
        val list = LinkedList(input.toList())
        var prev = '1'
        val it = list.listIterator()
        while (it.hasNext()) {
            val x = it.next()
            if (isOpposite(x, prev)) {
                it.remove()
                it.previous()
                it.remove()
                prev = if (it.hasPrevious()) it.previous() else '1'
            } else {
                prev = x
            }
        }
        return list.size
    }

    private fun isOpposite(a: Char, b: Char) = a.lowercaseChar() == b.lowercaseChar() && a != b
}