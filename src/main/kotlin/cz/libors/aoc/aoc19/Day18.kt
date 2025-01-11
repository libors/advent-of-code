package cz.libors.aoc.aoc19

import cz.libors.util.*

@Day("Many-Worlds Interpretation")
object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input18.txt").toPointsWithValue().toMap()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Map<Point, Char>): Int {
        val startPos = input.filter { it.value == '@' }.keys.single()
        val allKeysSet = setAllUpTo(input.values.filter { it.isLowerCase() }.distinct().size)
        val graph = computeGraph(input, listOf(startPos))

        fun neighbours(orig: Pair<Char, IntSet>): Iterable<Pair<Pair<Char, IntSet>, Int>> {
            val result = mutableListOf<Pair<Pair<Char, IntSet>, Int>>()
            for ((ch, dist) in graph[orig.first]!!) {
                val newKeys = neighbourWithKeys(orig.second, ch)
                if (newKeys != null) result.add((ch to newKeys) to dist)
            }
            return result
        }

        return dijkstra(Pair('0', 0), { it.second == allKeysSet }, ::neighbours).getScore()!!
    }

    private fun neighbourWithKeys(keys: IntSet, ch: Char): Int? = when (ch) {
        '.' -> keys
        in 'a'..'z' -> keys.set(ch - 'a')
        in 'A'..'Z' -> if (keys.isSet(ch.lowercaseChar() - 'a')) keys else null
        else -> null
    }

    private fun task2(input: Map<Point, Char>): Int {
        val starts = input.filter { it.value == '@' }.keys.single().diagonalPoints()
        val newMap = updateMap(input)
        val allKeysSet = setAllUpTo(input.values.filter { it.isLowerCase() }.distinct().size)
        val graph = computeGraph(newMap, starts)

        fun neighbours(orig: Node): Iterable<Pair<Node, Int>> {
            val result = mutableListOf<Pair<Node, Int>>()
            for (i in orig.robots.indices) {
                for ((ch, dist) in graph[orig.robots[i]]!!) {
                    val newKeys = neighbourWithKeys(orig.keys, ch)
                    if (newKeys != null) {
                        result.add(Node(orig.robots.mapIndexed { idx, v -> if (idx == i) ch else v }, newKeys) to dist)
                    }
                }
            }
            return result
        }

        return dijkstra(Node(listOf('0', '1', '2', '3'), 0), { it.keys == allKeysSet }, ::neighbours).getScore()!!
    }

    private data class Node(val robots: List<Char>, val keys: IntSet)

    private fun updateMap(input: Map<Point, Char>): Map<Point, Char> {
        val result = input.toMutableMap()
        val startPos = input.filter { it.value == '@' }.keys.single()
        result[startPos] = '#'
        startPos.neighbours().forEach { result[it] = '#' }
        return result
    }

    private fun computeGraph(map: Map<Point, Char>, starts: List<Point>): Map<Char, List<Pair<Char, Int>>> {

        fun paths(start: Point): List<Pair<Char, Int>> {
            val paths = bfsToAll(Pair(start, true)) { (pos, canGo) ->
                if (canGo) {
                    pos.neighbours()
                        .filter { n -> map[n]!!.let { ch -> ch == '.' || ch in 'a'..'z' || ch in 'A'..'Z' } }
                        .map { n -> Pair(n, map[n] == '.') }
                } else {
                    listOf()
                }
            }
            return paths.distances().filter { map[it.key.first]!! != '.' && it.key.first != start }
                .map { map[it.key.first]!! to it.value }
        }

        val fromLetters = map.filter { it.value.isLetter() }.keys.map { p -> map[p]!! to paths(p) }
        val fromStarts = starts.mapIndexed { idx, p -> idx.digitToChar() to paths(p) }
        return (fromLetters + fromStarts).toMap().mapValues { it.value.sortedBy { x -> x.first } }
    }
}