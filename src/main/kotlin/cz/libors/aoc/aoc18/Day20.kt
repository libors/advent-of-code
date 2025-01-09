package cz.libors.aoc.aoc18

import cz.libors.util.*
import java.util.LinkedList

@Day("A Regular Map")
object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input20.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String): Int {
        val map = buildMap(input)
        return  bfsToAll(Point(0, 0), { map[it]!! }).distances().maxOf { it.value }
    }

    private fun task2(input:String): Int {
        val map = buildMap(input)
        return bfsToAll(Point(0, 0), { map[it]!! }).distances().count { it.value >= 1000 }
    }

    private fun buildMap(input: String): Map<Point, List<Point>> {
        val result = multiMap<Point, Point>()
        val stack = LinkedList<Point>()
        var pos = Point(0, 0)
        for (char in input) {
            when (char) {
                'N', 'W', 'S', 'E' -> {
                    val newPos = pos + Vector.from(char.toString())!!
                    result.add(pos, newPos)
                    result.add(newPos, pos)
                    pos = newPos
                }
                '(' -> stack.push(pos)
                '|' -> pos = stack.peek()
                ')' -> pos = stack.pop()
            }
        }
        return result.mapValues { it.value.distinct() }
    }
}