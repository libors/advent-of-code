package cz.libors.aoc.aoc22

import cz.libors.util.Day
import cz.libors.util.Interval
import cz.libors.util.findPositiveInts
import cz.libors.util.readToLines

@Day(name = "Camp Cleanup")
object Day4 {

    private fun toIntervals(s: String) = s
        .findPositiveInts()
        .map { it.toLong() }
        .let { listOf(Interval(it[0], it[1]), Interval(it[2], it[3])) }

    private fun task1(intervals: List<List<Interval>>) = intervals
        .count { it[0].isSubOf(it[1]) || it[1].isSubOf(it[0]) }

    private fun task2(intervals: List<List<Interval>>) = intervals
        .count { it[0].overlaps(it[1]) }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input4.txt")
            .map { toIntervals(it) }
        println(task1(input))
        println(task2(input))
    }
}