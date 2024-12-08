package cz.libors.aoc.aoc24

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines
import kotlin.math.abs

@Day("Red-Nosed Reports")
object Day2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input2.txt")
            .map { it.findInts() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(list: List<List<Int>>) = list.count { safe(it) }
    private fun task2(list: List<List<Int>>) = list.count { safe2(it) }

    private fun safe(list: List<Int>): Boolean {
        for (i in 0 until list.lastIndex) {
            val diff = abs(list[i] - list[i + 1])
            if (diff > 3 || diff < 1) {
                return false
            }
        }
        return if (list[0] < list[1]) list.sorted() == list else list.sorted().reversed() == list
    }

    private fun safe2(list: List<Int>): Boolean {
        if (safe(list)) {
            return true
        }
        for (i in 0 .. list.lastIndex) {
            val newList = list.toMutableList()
            newList.removeAt(i)
            if (safe(newList)) {
                return true
            }
        }
        return false
    }
}