package cz.libors.aoc.aoc24

import cz.libors.util.*
import kotlin.collections.HashSet
import kotlin.time.measureTime

@Day("LAN Party")
object Day23 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input23.txt")
            .map { val sides = it.findAlphanums(); Pair(sides[0], sides[1]) }
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
        val vertices = nodes.keys.toMutableSet()

        fun <TT: T> bronKerbosch(clique: Set<TT>, potential: MutableSet<TT>, rejected: MutableSet<TT>): Set<Set<TT>> {
            val cliques = mutableSetOf<Set<TT>>()
            if (potential.isEmpty() && rejected.isEmpty()) {
                cliques.add(HashSet(clique))
            }
            while (potential.isNotEmpty()) {
                val node = potential.iterator().next()
                val newClique = HashSet(clique)
                newClique.add(node)
                val newPotential = HashSet(potential)
                newPotential.retainAll(graph[node]!!)
                val newNotInClique = HashSet(rejected)
                newNotInClique.retainAll(graph[node]!!)
                cliques.addAll(bronKerbosch(newClique, newPotential, newNotInClique))
                potential.remove(node)
                rejected.add(node)
            }
            return cliques
        }

        return bronKerbosch(mutableSetOf(), vertices, mutableSetOf())
    }
}