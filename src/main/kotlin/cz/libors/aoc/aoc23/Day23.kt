package cz.libors.aoc.aoc23

import cz.libors.util.*

@Day(name = "A Long Walk")
object Day23 {

    @JvmStatic
    fun main(args: Array<String>) {
        val timer = Timer()
        val input = readToLines("input23.txt").toPointsWithValue()
            .filter { it.second != '#' }
            .toMap()
        println(task1(input))
        timer.measure("task1")
        println(task2(input))
        timer.measure("task2")
    }

    private data class Edge(val target: Int, val distance: Int)
    private data class Graph(val pointMap: Map<Point, Int>, val nodes: Array<Array<Edge>>)

    private fun toGraph(maze: Map<Point, Char>, useSlopes: Boolean): Graph {
        val box = maze.keys.boundingBox()
        val start = maze.entries.first { it.key.y == box.first.y && it.value == '.' }.key
        val end = maze.entries.first { it.key.y == box.second.y && it.value == '.' }.key

        val points = maze.filter { ".>v".contains(it.value) }.keys
        val crossroads = (points.filter { p ->
            p.neighbours().filter { a -> points.contains(a) }.size > 2
        } + start + end).toSet()
        val pointMap = crossroads.toList().mapIndexed { idx, p -> p to idx }.toMap()
        val mazeWithCrossroads = maze + crossroads.associateWith { 'x' }

        val pointNodes = crossroads.associate { sp ->
            pointMap[sp]!! to sp.neighbours()
                .filter { points.contains(it) && !crossroads.contains(it) }
                .mapNotNull { adj -> findSingleTrack(sp, adj, mazeWithCrossroads, useSlopes) }
                .map { Edge(pointMap[it.first]!!, it.second) }
                .toTypedArray()
        }
        val nodes = Array(crossroads.size) { pointNodes[it]!! }
        return Graph(pointMap, nodes)
    }

    private fun findSingleTrack(initCrossroad: Point, start: Point, maze: Map<Point, Char>, useSlopes: Boolean): Pair<Point, Int>? {
        val path = bfs(start, { maze[it] == 'x'}, { it.neighbours().filter { x -> maze.containsKey(x) && x != initCrossroad } })
        if (path.getPath().isEmpty()) return null
        if (useSlopes) {
            var previous = initCrossroad
            for (point in path.getPath()) {
                if (maze[point] == '>' && point.left() != previous) return null
                if (maze[point] == 'v' && point.up() != previous) return null
                previous = point
            }
        }
        return Pair(path.getPath().last(), path.getScore()!! + 1)
    }

    private fun task1(input: Map<Point, Char>) = Finder(input, true).find()
    private fun task2(input: Map<Point, Char>) = Finder(input, false).find()

    private class Finder(maze: Map<Point, Char>, useSlopes: Boolean) {
        private val start: Int
        private val end: Int
        private val currentPath = mutableListOf<Int>()
        private val nodes: Array<Array<Edge>>

        init {
            val (pointMap, nodes) = toGraph(maze, useSlopes)
            val box = maze.keys.boundingBox()
            start = pointMap[maze.entries.first { it.key.y == box.first.y && it.value == '.' }.key]!!
            end = pointMap[maze.entries.first { it.key.y == box.second.y && it.value == '.' }.key]!!
            this.nodes = nodes
        }

        fun find() = findWithoutSlopes(start, 0)

        fun findWithoutSlopes(x: Int, length: Int): Int {
            if (x == end) return length
            var max = 0
            for (option in nodes[x]) {
                if (!currentPath.contains(option.target)) {
                    currentPath.add(option.target)
                    val result = findWithoutSlopes(option.target, length + option.distance)
                    if (result > max) max = result
                    currentPath.remove(option.target)
                }
            }
            return max
        }
    }
}