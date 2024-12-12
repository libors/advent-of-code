package cz.libors.aoc.aoc24

import cz.libors.util.*

private typealias Maze = Map<Point, Int>

@Day("Hoof It")
object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input10.txt").toPointsWithValue()
            .associate { Pair(it.first, it.second.digitToInt()) }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(map: Maze): Int {
        val starts = map.filter { it.value == 0 }.keys
        val ends = map.filter { it.value == 9 }.keys
        return starts.sumOf { start -> ends.count { end -> existsPath(start, end, map) } }
    }

    private fun task2(map: Maze) = map.filter { it.value == 0 }.keys.sumOf { countPaths(it, map) }

    private fun existsPath(start: Point, end: Point, map: Maze) = bfs(start, { it == end }) { it.paths(map) }.hasPath()

    private fun Point.paths(map: Maze) = this.neighbours().filter { map[it] == map[this]!! + 1 }

    private fun countPaths(p: Point, map: Maze): Int = when {
        map[p] == 9 -> 1
        else -> p.paths(map).sumOf { countPaths(it, map) }
    }
}