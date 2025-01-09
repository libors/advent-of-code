package cz.libors.aoc.aoc15

import cz.libors.util.*

private typealias Lights = Map<Point, Boolean>

@Day("Like a GIF For Your Yard")
object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input18.txt").toPointsWithValue().associate { Pair(it.first, it.second == '#') }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Lights): Int {
        var x = input
        repeat(100) { x = derive(x) }
        return x.count { it.value }
    }

    private fun task2(input: Lights): Int {
        val corners = input.keys.boundingBox().let {
            listOf(it.first, it.second, Point(it.first.x, it.second.y), Point(it.second.x, it.first.y))
        }
        var x = turnOn(input, corners)
        repeat(100) { x = turnOn(derive(x), corners) }
        return x.count { it.value }
    }

    private fun turnOn(lights: Lights, on: List<Point>) = lights.mapValues { it.key in on || it.value }

    private fun derive(lights: Lights) = lights.mapValues { (p, v) ->
        val neighboursOn = p.neighbours(true).count { n -> lights[n] == true }
        if (v) neighboursOn in 2..3 else neighboursOn == 3
    }
}