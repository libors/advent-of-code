package cz.libors.aoc.aoc18

import cz.libors.util.*

@Day("No Matter How You Slice It")
object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input3.txt").map {it.findInts().let { i -> Item(i[0], Point(i[1], i[2]), Point(i[3], i[4])) } }
        val appliedClaims = applyClaims(input)
        println(task1(appliedClaims))
        println(task2(appliedClaims, input.size))
    }

    private fun applyClaims(input: List<Item>): MultiMap<Point, Int> {
        val map = multiMap<Point, Int>()
        for (i in input) {
            for (x in i.start.x until i.start.x + i.size.x)
                for (y in i.start.y until i.start.y + i.size.y)
                    map.add(Point(x, y), i.id)
        }
        return map
    }

    private fun task1(map: MultiMap<Point, Int>) = map.count { it.value.size > 1 }

    private fun task2(map: MultiMap<Point, Int>, claimsNum: Int): Int {
        val overlaps = BooleanArray(claimsNum)
        map.values.filter { it.size > 1 }.forEach { v -> v.forEach { overlaps[it - 1] = true } }
        return overlaps.indexOfFirst { !it } + 1
    }

    private data class Item(val id: Int, val start: Point, val size: Point)
}