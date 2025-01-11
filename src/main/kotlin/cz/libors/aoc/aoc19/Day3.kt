package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.Point
import cz.libors.util.Vector
import cz.libors.util.readToLines

@Day("Crossed Wires")
object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input3.txt").map { line ->
            line.split(",").map { Pair(Vector.from(it[0].toString())!!, it.substring(1).toInt()) }
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<List<Pair<Vector, Int>>>) = findIntersections(input)
        .keys.minOf { it.manhattanDistance(Point(0, 0)) }

    private fun task2(input: List<List<Pair<Vector, Int>>>) = findIntersections(input)
        .values.minOf { sumTimes(it) }

    private fun sumTimes(intersection: List<Position>) = intersection.groupBy { it.wire }
        .map { w -> w.value.minOf { it.time } }.sum()

    private fun findIntersections(input: List<List<Pair<Vector, Int>>>) =
        input.flatMapIndexed { idx, moves -> go(idx, moves) }
            .groupBy { it.pos }
            .filter { group -> group.value.any { it.wire == 0 } && group.value.any { it.wire == 1 } }

    private fun go(wire: Int, moves: List<Pair<Vector, Int>>): List<Position> {
        var pos = Point(0, 0)
        var i = 0
        val result = mutableListOf<Position>()
        moves.forEach { (dir, cnt) ->
            repeat(cnt) {
                pos += dir
                result.add(Position(wire, pos, ++i))
            }
        }
        return result
    }

    private data class Position(val wire: Int, val pos: Point, val time: Int)
}