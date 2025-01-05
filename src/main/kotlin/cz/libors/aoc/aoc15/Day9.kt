package cz.libors.aoc.aoc15

import cz.libors.util.*

@Day("All in a Single Night")
object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = createGraph(readToLines("input9.txt"))
        println(task1(input))
        println(task2(input))
    }

    private fun task1(graph: MultiMap<String, Pair<String, Int>>) = graph.keys.minOf { findLength(graph, it) }
    private fun task2(graph: MultiMap<String, Pair<String, Int>>) = graph.keys.maxOf { findLength(graph, it, false) }

    private fun findLength(graph: MultiMap<String, Pair<String, Int>>, start: String, min: Boolean = true): Int {
        val visited = mutableSetOf<String>()

        fun dfs(x: String, runningLength: Int): Int {
            visited.add(x)
            if (visited.size == graph.keys.size) {
                visited.remove(x)
                return runningLength
            }
            val neighbors = graph[x]!!.filter { !visited.contains(it.first) }
            if (neighbors.isEmpty()) {
                visited.remove(x)
                return if (min) Int.MAX_VALUE else Int.MIN_VALUE
            }
            val result = if (min) {
                neighbors.minOf { dfs(it.first, runningLength + it.second) }
            } else {
                neighbors.maxOf { dfs(it.first, runningLength + it.second) }
            }
            visited.remove(x)
            return result
        }

        return dfs(start, 0)
    }

    private fun createGraph(input: List<String>): MultiMap<String, Pair<String, Int>> {
        val result = multiMap<String, Pair<String, Int>>()
        for (i in input) {
            val split = i.split(" ")
            val from = split[0]
            val to = split[2]
            val dist = split[4].toInt()
            result.add(from, to to dist)
            result.add(to, from to dist)
        }
        return result
    }
}