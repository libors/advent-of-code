package cz.libors.aoc.aoc21

import cz.libors.util.*

@Day("Passage Pathing")
object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input12.txt").map { it.findAlphanums() }
        val g = createGraph(input)
        println(task1(g))
        println(task2(g))
    }

    private fun createGraph(input: List<List<String>>): Graph {
        val caves = multiMap<String, String>()
        input.forEach { caves.add(it[0], it[1]); caves.add(it[1], it[0]) }
        val indices = caves.keys.mapIndexed { index, s -> s to index }.toMap()
        val nodes = indices.entries.toList().sortedBy { it.value }.map {
            Node(
                it.value, it.key[0].isLowerCase(), it.key == "end",
                caves[it.key]!!.distinct().map { x -> indices[x]!! }.toTypedArray()
            )
        }.toTypedArray()
        return Graph(indices["start"]!!, nodes)
    }

    private fun task1(g: Graph) = Finder(g, false).find()
    private fun task2(g: Graph) = Finder(g, true).find()

    private class Finder(graph: Graph, val canSecondVisit: Boolean) {
        private val visited: Array<Boolean> = Array(graph.nodes.size) { false }
        private val nodes = graph.nodes
        private val start = graph.start
        private var paths = 0
        private var usedSecond = false

        fun find(): Int {
            dfs(nodes[start])
            return paths
        }

        private fun dfs(n: Node) {
            if (n.end) {
                paths++
                return
            }
            var removeSecond = false
            if (n.small) {
                if (visited[n.id]) {
                    usedSecond = true
                    removeSecond = true
                }
                visited[n.id] = true
            }
            for (option in n.others) {
                val o = nodes[option]
                if (!o.small || !visited[option] || (canSecondVisit && !usedSecond && option != start)) dfs(o)
            }
            if (n.small) {
                if (removeSecond)
                    usedSecond = false
                else
                    visited[n.id] = false
            }
        }
    }

    private class Graph(val start: Int, val nodes: Array<Node>)
    private class Node(val id: Int, val small: Boolean, val end: Boolean, val others: Array<Int>)
}