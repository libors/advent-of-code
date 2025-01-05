package cz.libors.aoc.aoc16

import cz.libors.util.Day
import cz.libors.util.Interval
import cz.libors.util.findLongs
import cz.libors.util.readToLines

@Day("Firewall Rules")
object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input20.txt").map { line -> line.findLongs(true).let { Interval(it[0], it[1]) } }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(intervals: List<Interval>): Long {
        val candidates = intervals.map { it.from - 1 }.filter { it >= 0 }.sorted()
        return candidates.find { c -> intervals.none { it.contains(c) } }!!
    }

    private fun task2(input: List<Interval>): Long {
        val merged = mergeIntervals(input)
        var sum = merged[0].from
        for (i in 0 until merged.size - 1) {
            sum += merged[i + 1].from - merged[i].to - 1
        }
        sum += 4294967295 - merged.last().to
        return sum
    }

    private fun mergeIntervals(input: List<Interval>): List<Interval> {
        val sorted = input.sortedBy { it.from }
        val merged = mutableListOf<Interval>()
        var prev = sorted[0]
        for (i in 1 until sorted.size) {
            val interval = sorted[i]
            if (prev.overlaps(interval)) {
                prev = prev.union(interval)
            } else {
                merged.add(prev)
                prev = interval
            }
        }
        merged.add(prev)
        return merged
    }
}