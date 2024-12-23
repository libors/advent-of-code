package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("LAN Party")
object Day23 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input23.txt").map { it.findAlphanums().let { ints -> Pair(ints[0], ints[1]) } }
        val graph = createGraph(input)
        println(task1(graph))
        println(task2(graph))
    }

    private fun task1(graph: MultiMap<String, String>): Int {
        val result = mutableSetOf<List<String>>()
        for ((node1, neighbours) in graph.filter { it.key.startsWith('t') }) {
            for (node2Idx in neighbours.indices) {
                val node2 = neighbours[node2Idx]
                for (node3Idx in node2Idx + 1 until neighbours.size) {
                    val node3 = neighbours[node3Idx]
                    if (node2 != node3 && graph[node2]!!.contains(node3)) {
                        result.add(listOf(node1, node2, node3).sorted())
                    }
                }
            }
        }
        return result.size
    }

    private fun task2(graph: MultiMap<String, String>) = findCliques(graph)
        .maxBy { it.size }.toList().sorted().joinToString(",")

    private fun createGraph(nodes: List<Pair<String, String>>): MultiMap<String, String> {
        val graph: MultiMap<String, String> = mutableMapOf()
        for (connection in nodes) {
            graph.add(connection.first, connection.second)
            graph.add(connection.second, connection.first)
        }
        return graph
    }

    private fun <T> findCliques(nodes: MultiMap<T, T>): Set<Set<T>> {
        val graph = nodes.mapValues { it.value.toSet() }
        val result = mutableSetOf<Set<T>>()

        fun <TT: T> bronKerbosch(clique: List<TT>, potential: MutableList<TT>, rejected: MutableList<TT>) {
            if (potential.isEmpty() && rejected.isEmpty()) {
                result.add(HashSet(clique))
            }
            while (potential.isNotEmpty()) {
                val node = potential.last()
                val newPotential = ArrayList(potential)
                newPotential.retainAll(graph[node]!!)
                val newRejected = ArrayList(rejected)
                newRejected.retainAll(graph[node]!!)
                bronKerbosch(clique + node, newPotential, newRejected)
                potential.removeLast()
                rejected.add(node)
            }
        }

        bronKerbosch(listOf(), nodes.keys.toMutableList(), mutableListOf())
        return result
    }
}