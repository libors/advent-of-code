package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("RAM Run")
object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input18.txt").map { it.findInts() }.map { Point(it[0], it[1]) }
        val box = Pair(Point(0, 0), Point(70, 70))
        println(task1(box, input))
        println(task2(box, input).let { it.x.toString() + "," + it.y.toString() })
    }

    private fun task1(box: Box, input: List<Point>) = shortestPath(box, input.take(1024).toSet()).getScore()

    private fun task2(box: Box, input: List<Point>): Point {
        var left = 1024
        var right = input.size
        while (right - left > 1) {
            val middle = (left + right) / 2
            if (shortestPath(box, input.take(middle).toSet()).hasPath()) left = middle else right = middle
        }
        return input[right - 1]
    }

    private fun shortestPath(box: Box, corrupted: Set<Point>) = bfs(box.first, endFn = { it == box.second },
        neighboursFn = { it.neighbours().filter { n -> box.contains(n) && !corrupted.contains(n) } })
}