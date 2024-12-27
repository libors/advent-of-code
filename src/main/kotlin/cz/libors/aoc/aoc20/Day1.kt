package cz.libors.aoc.aoc20

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Report Repair")
object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input1.txt").map { it.toInt() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Int>) = find(input, 1, 0)
    private fun task2(input: List<Int>) = find(input, 2, 0)

    private fun find(input: List<Int>, remain: Int, runningSum: Int): Int {
        for (i in input) {
            val newRunning = runningSum + i
            if (remain == 0) {
                if (newRunning == 2020) return i
            } else {
                val x = if (newRunning > 2020) 0 else find(input, remain - 1, newRunning)
                if (x > 0) return x * i
            }
        }
        return 0
    }
}