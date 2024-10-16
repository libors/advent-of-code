package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToText

@Day("Lanternfish")
object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input6.txt").findInts()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Int>) = countFishes(input, 80)
    private fun task2(input: List<Int>) = countFishes(input, 256)

    private fun countFishes(input: List<Int>, generations: Int): Long {
        val cycleAge = 6
        val newbornCycleAge = 8
        var generation = input.groupingBy { it }.eachCount().mapValues { it.value.toLong() }
        for (i in 1..generations) {
            val newGen = mutableMapOf<Int, Long>()
            for ((age, cnt) in generation) {
                if (age == 0) {
                    newGen.merge(cycleAge, cnt, Long::plus)
                    newGen[newbornCycleAge] = cnt
                } else {
                    newGen.merge(age - 1, cnt, Long::plus)
                }
            }
            generation = newGen
        }
        return generation.values.sumOf { it }
    }
}