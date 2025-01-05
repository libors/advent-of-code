package cz.libors.aoc.aoc20

import cz.libors.util.readToLines

object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input13.txt")
        println(task1(input))
        TODO("chinese remainder theorem for part 2")
    }

    private fun task1(input: List<String>): Int {
        val estimate = input[0].toInt()
        val buses = input[1].split(",").mapNotNull { it.toIntOrNull() }
        val departs = buses.map {
            val closest = estimate - (estimate % it)
            Pair(it, if (closest == estimate) closest else closest + it)
        }
        return departs.minBy { it.second }.let { it.first * (it.second - estimate) }
    }
}