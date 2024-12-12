package cz.libors.aoc.aoc23

import cz.libors.util.*

@Day(name = "Step Counter")
object Day21 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input21.txt").toPointsWithValue().toMap()
        println(task1(input))
        TODO("TODO, diamond, hard, not implemented yet")
        //println(task2(input))
    }

    private fun task1(input: Map<Point, Char>): Int {
        var positions = listOf(input.entries.first { it.value == 'S' }.key)
        for (i in 1..10) {
            positions = positions.flatMap {
                it.neighbours().filter { p -> input[p] == '.' || input[p] == 'S' }
            }.distinct()
        }
        return positions.size
    }

    private fun task2(input: Map<Point, Char>): Int {
        val size = input.keys.boundingBox().second + Vector.RIGHT_DOWN
        var positions = listOf(input.entries.first { it.value == 'S' }.key)
        var prev = 0
        val counts = mutableMapOf<Int, Int>()
        for (i in 1..500) {
            positions = positions.flatMap { it.neighbours().filter { p ->
                val moduled = Point(p.x.posMod(size.x), p.y.posMod(size.y))
                input[moduled] == '.' || input[moduled] == 'S' }
            }.distinct()
            if (i % 100 == 0) println(positions.size - prev)
            counts.merge(positions.size - prev, 1) { _, v -> v + 1 }
            prev = positions.size
        }
        return 0
    }

}