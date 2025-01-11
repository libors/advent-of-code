package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("The Tyranny of the Rocket Equation")
object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input1.txt").map { it.toInt() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Int>) = input.sumOf { it / 3 - 2 }
    private fun task2(input: List<Int>) = input.sumOf { fuelToStart2(it) }

    private fun fuelToStart(mass: Int) = mass / 3 - 2

    private fun fuelToStart2(mass: Int): Int {
        fun count(m: Int, counter: Int): Int = fuelToStart(m).let { if (it <= 0) counter else count(it, counter + it) }
        return count(mass, 0)
    }
}