package cz.libors.aoc.aoc23

import cz.libors.util.Day
import cz.libors.util.findAlphanums
import cz.libors.util.readToLines
import java.util.*

private typealias Edge = Pair<String, String>

@Day(name = "Snowverload")
object Day25 {

    private val random = Random()

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("test.txt").flatMap {
            val items = it.findAlphanums()
            items.subList(1, items.size).map { i -> i to items[0] }
        }
        val vertices = (input.map { it.first } + input.map { it.second }).toSet()
        println("pairs: ${input.size}, vertices: ${vertices.size}")
        val counts = vertices.associateWith { v -> input.count { it.first == v || it.second == v } }
        val stats = counts.entries.groupingBy { it.value }.eachCount()
        println(stats)

        val x = vertices.associateWith { v -> input.mapNotNull { if (it.first == v) it.second else if (it.second == v) it.first else null } }
        x.forEach { (k, v) -> println("$k -> $v") }

        TODO("Not implemented yet")
    }


    fun solution(number: Int): Int = (1 until number).filter { it % 3 == 0 && it % 5 == 0}.sumOf { it }

    private fun runKarger(edges: List<Pair<String, String>>) {

    }

//    private fun runKargerIteration(graph: Map<String, LinkedList<Pair<Edge, String>>>): List<Edge> {
//        val nodes = LinkedList(graph.keys)
//        while (graph.size > 2) {
//            val randomNodeIdx = random.nextInt(nodes.size)
//            val n1Name = nodes[randomNodeIdx]
//            val n1 = graph[n1Name]!!
//            val randomEdgeIdx = random.nextInt(n1.size)
//            val randomEdge = n1[randomEdgeIdx]
//            val n2Name = randomEdge.second
//            val n2 = graph[n2Name]!!
//            for (edge in n2) {
//                val otherNode = graph[edge.second]!!
//                otherNode.removeIf { it.second == n2Name }
//                otherNode.add(Pair(edge, ))
//            }
//
//
//        }


//    }


}