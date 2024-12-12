package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.Point
import cz.libors.util.readToLines
import cz.libors.util.toPointsWithValue

@Day("Dumbo Octopus")
object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input11.txt").toPointsWithValue().map { it.first to it.second.digitToInt() }.toMap()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Map<Point, Int>): Int {
        val octopi = input.toMutableMap()
        var flashes = 0
        for (step in 1..100) {
            flashes += flashInStep(octopi)
        }
        return flashes
    }

    private fun task2(input: Map<Point, Int>): Int {
        val octopi = input.toMutableMap()
        var step = 0
        while(true) {
            step++
            if (flashInStep(octopi) == octopi.size) return step
        }
    }

    private fun flashInStep(octopi: MutableMap<Point, Int>): Int {
        octopi.forEach { octopi[it.key] = it.value + 1 }
        octopi.filter { it.value == 10 }.forEach { flash(octopi, it.key) }
        val flashed = octopi.filter { it.value >= 10 }
        flashed.forEach { octopi[it.key] = 0 }
        return flashed.size
    }

    private fun flash(octopi: MutableMap<Point, Int>, octopus: Point) {
        val neighbours = octopus.neighbours(alsoDiag = true)
        for (neighbour in neighbours) {
            val energy = octopi[neighbour]
            if (energy != null) {
                octopi[neighbour] = energy + 1
                if (energy + 1 == 10) flash(octopi, neighbour)
            }
        }
    }
}