package cz.libors.aoc.aoc23

import cz.libors.util.*
import cz.libors.util.Vector

@Day(name = "A Long Walk")
object Day23 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input23.txt").toPointsWithValue().toMap()
        println(task1(input))
        println(task2(input))
    }

    private fun toGraph(maze: Map<Point, Char>): Map<Point, List<Pair<Point, Int>>> {
        val box = maze.keys.boundingBox()
        val start = maze.entries.first { it.key.y == box.first.y && it.value == '.' }.key
        val end = maze.entries.first { it.key.y == box.second.y && it.value == '.' }.key

        val points = maze.filter { ".>v".contains(it.value) }.keys
        val significantPoints = (points.filter { p ->
            p.adjacentPoints().filter { a -> points.contains(a) }.size > 2
        } + start + end).toSet()
        val prepare = mutableMapOf<Point, Char>()
        points.forEach { prepare[it] = '.' }
        significantPoints.forEach { prepare[it] = 'x' }
        return significantPoints.map { sp ->
            sp to sp.adjacentPoints()
                .filter { points.contains(it) && !significantPoints.contains(it) }
                .mapNotNull { adj -> findSingleTrack(sp, adj, prepare) }
        }.toMap()
    }

    private fun task1(input: Map<Point, Char>) = Finder(input).find(true)
    private fun task2(input: Map<Point, Char>) = Finder(input).find(false)

    private class Finder(val maze: Map<Point, Char>) {
        private val start: Point
        private val end: Point
        private val currentPath = mutableListOf<Point>()
        private val g = toGraph(maze)

        init {
            val box = maze.keys.boundingBox()
            start = maze.entries.first { it.key.y == box.first.y && it.value == '.' }.key
            end = maze.entries.first { it.key.y == box.second.y && it.value == '.' }.key
        }

        fun find(withSlopes: Boolean): Int {
            return if (withSlopes) findWithSlopes(start, 0) else findWithoutSlopes(start, 0)
        }

        fun findWithoutSlopes(x: Point, length: Int): Int {
            if (x == end) return length
            val options = g[x]!!.filter { !currentPath.contains(it.first) }
                .map {
                    currentPath.add(it.first)
                    val result = findWithoutSlopes(it.first, length + it.second)
                    currentPath.remove(it.first)
                    result
                }
            return if (options.isEmpty()) 0 else options.maxOf { it }
        }

        private fun findWithSlopes(x: Point, length: Int): Int {
            if (x == end) {
                return length
            }
            val options = mutableListOf<Int>()
            val type = maze[x]
            val paths = when (type) {
                '.' -> x.adjacentPoints()
                '>' -> listOf(x.plus(Vector.RIGHT))
                'v' -> listOf(x.plus(Vector.DOWN))
                else -> emptyList()
            }
            for (p in paths) {
                val pType = maze[x]
                if (pType == '.' || (pType == '>' && p != x.plus(Vector.LEFT)) || (pType == 'v' && p != x.plus(Vector.UP))) {
                    if (!currentPath.contains(p)) {
                        currentPath.add(p)
                        options.add(findWithSlopes(p, length + 1))
                        currentPath.remove(p)
                    }
                }
            }
            return if (options.isEmpty()) 0 else options.maxOf { it }
        }
    }

}