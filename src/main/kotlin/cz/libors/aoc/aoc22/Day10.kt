package cz.libors.aoc.aoc22

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day(name = "Cathode-Ray Tube")
object Day10 {

    private fun runInstructions(input: List<String>): List<Int> {
        var x = 1
        val result = mutableListOf<Int>()
        result.add(x)
        for (inst in input) {
            if (inst == "noop") {
                result.add(x)
            } else {
                val num = inst.substringAfter(' ').toInt()
                result.add(x)
                x += num
                result.add(x)
            }
        }
        return result
    }

    private fun task1(input: List<String>): Int {
        val xList = runInstructions(input)
        return listOf(20, 60, 100, 140, 180, 220).sumOf { xList[it - 1] * it }
    }

    private fun task2(input: List<String>) {
        val xList = runInstructions(input)
        for (i in xList.indices) {
            val xPos = i % 40
            val num = xList[i]
            val pixel = if (xPos == num || xPos + 1 == num || xPos - 1 == num) "#" else " "
            print(pixel)
            if ((i+1) % 40 == 0) println()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input10.txt")
        println(task1(input))
        task2(input)
    }
}