package cz.libors.aoc.aoc15

import cz.libors.util.*
import kotlin.math.max

@Day("Probably a Fire Hazard")
object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input6.txt").map { line ->
            val box = line.findInts().let { Box(Point(it[0], it[1]), Point(it[2], it[3])) }
            val switch = when {
                line.startsWith("turn on") -> 1
                line.startsWith("turn off") -> -1
                line.startsWith("toggle") -> 0
                else -> throw Exception()
            }
            Pair(box, switch)
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Pair<Box, Int>>) = operateLights(input, ::switch)
    private fun task2(input: List<Pair<Box, Int>>) = operateLights(input, ::dim)

    private fun switch(x: Int, v: Int) = when (v) {
        -1 -> 0
        1 -> 1
        else -> if (x == 1) 0 else 1
    }

    private fun dim(x: Int, v: Int) = when (v) {
        -1 -> max(0, x - 1)
        1 -> x + 1
        else -> x + 2
    }

    private fun operateLights(input: List<Pair<Box, Int>>, fn: (Int, Int) -> Int): Long {
        val lights = Array(1000) { IntArray(1000) }
        for (i in input)
            for (x in i.first.first.x..i.first.second.x)
                for (y in i.first.first.y..i.first.second.y)
                    lights[x][y] = fn(lights[x][y], i.second)

        var cnt = 0L
        for (column in lights)
            for (row in column.indices)
                cnt += column[row]

        return cnt
    }
}