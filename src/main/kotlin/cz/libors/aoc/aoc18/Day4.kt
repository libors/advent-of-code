package cz.libors.aoc.aoc18

import cz.libors.util.*

@Day("Repose Record")
object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input4.txt").sortedBy { it.substring(1, 17) }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>): Int {
        val sleepMap = createSleepMap(input)
        val mostAsleepGuard = sleepMap.maxBy { (_, v) -> v.sumOf { it.second - it.first } }.key
        val mostSleepMin = createSleepMinuteMap(sleepMap[mostAsleepGuard]!!).maxBy { it.value }.key
        return mostAsleepGuard * mostSleepMin
    }

    private fun task2(input: List<String>): Int {
        val sleepMinuteMapPerGuard = createSleepMap(input).mapValues { createSleepMinuteMap(it.value) }
        var maxMin = -1
        var maxMinSize = -1
        var maxGuard = -1
        for (g in sleepMinuteMapPerGuard) {
            for (m in g.value) {
                if (m.value > maxMinSize) {
                    maxMinSize = m.value
                    maxMin = m.key
                    maxGuard = g.key
                }
            }
        }
        return maxGuard * maxMin
    }

    private fun createSleepMinuteMap(sleepIntervals: List<Pair<Int, Int>>): Map<Int, Int> {
        val result = mutableMapOf<Int, Int>()
        for (sleepInterval in sleepIntervals) {
            for (i in sleepInterval.first..sleepInterval.second)
                result.merge(i, 1, Int::plus)
        }
        return result
    }

    // guard id -> List<Pair<sleepFromMin, sleepToMin>>
    private fun createSleepMap(input: List<String>): MultiMap<Int, Pair<Int, Int>> {
        var guard = -1
        var sleepMinute = -1
        val sleepMap = multiMap<Int, Pair<Int, Int>>()
        for (line in input) {
            when {
                line.contains("begins shift") -> guard = line.substring(20).findInts()[0]
                line.contains("wakes") -> sleepMap.add(guard, Pair(sleepMinute, line.substring(15, 17).toInt() -1))
                line.contains("asleep") -> sleepMinute = line.substring(15, 17).toInt()
                else -> throw IllegalArgumentException()
            }
        }
        return sleepMap
    }
}