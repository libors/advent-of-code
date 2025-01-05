package cz.libors.aoc.aoc18

import cz.libors.util.*
import java.awt.Color

@Day("Settlers of The North Pole")
object Day18 {

    private const val LONG_TIME = 1000000000

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input18.txt").toPointsWithValue().toMap()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Map<Point, Char>): Int {
        var map = input
        repeat(10) { map = transform(map) }
        return resourceScore(map)
    }

    private fun task2(input: Map<Point, Char>): Int {
        val history = mutableMapOf<Map<Point, Char>, Int>()
        var map = input
        var same = Pair(0, 0)
        val g = Graphics(displayLabels = false, minBoundingBox = input.keys.boundingBox(), charOrder = "#|",
            colorSchema = ColorSchemas.staticColors(listOf(Color.RED, Color.GREEN)), delay = 20)
        for (i in 1..LONG_TIME) {
            //g.showChars(map.filter { it.value != '.' })
            map = transform(map)
            if (history.containsKey(map)) {
                same = Pair(history[map]!!, i)
                break
            }
            history[map] = i
        }
        val historyIdx = (LONG_TIME - same.first) % (same.second - same.first) + same.first
        return resourceScore(history.filter { it.value == historyIdx }.toList()[0].first)
    }

    private fun resourceScore(map: Map<Point, Char>) = map.count { it.value == '#' } * map.count { it.value == '|' }

    private fun transform(map: Map<Point, Char>): Map<Point, Char> {
        val result = mutableMapOf<Point, Char>()
        for ((k, v) in map) {
            when (v) {
                '|' -> result[k] = if(k.neighbours(true).count { map[it] == '#' } >= 3) '#' else '|'
                '#' -> result[k] = if(k.neighbours(true).any { map[it] == '#' } && k.neighbours(true).any { map[it] == '|' }) '#' else '.'
                '.' -> result[k] = if(k.neighbours(true).count { map[it] == '|' } >= 3) '|' else '.'
            }
        }
        return result
    }
}