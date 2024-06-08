package cz.libors.aoc.aoc22

import cz.libors.util.Day
import cz.libors.util.readToText
import java.util.*

@Day(name = "Tuning Trouble")
object Day6 {

    private fun distinctIndex(input: String, num: Int): Int {
        val buffer: Deque<Char> = LinkedList()
        for (i in input.indices) {
            buffer.addLast(input[i])
            if (buffer.size > num) buffer.removeFirst()
            if (buffer.distinct().size == num) return i + 1
        }
        throw java.lang.IllegalArgumentException()
    }

    private fun task1(input: String) = distinctIndex(input, 4)
    private fun task2(input: String) = distinctIndex(input, 14)

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input6.txt")
        println(task1(input))
        println(task2(input))
    }
}