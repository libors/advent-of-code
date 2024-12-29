package cz.libors.aoc.aoc18

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToText

@Day("Memory Maneuver")
object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input8.txt").findInts()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Int>) = buildTree(input).sumMeta()
    private fun task2(input: List<Int>) = buildTree(input).nodeValue()

    private fun buildTree(input: List<Int>): Node {

        fun buildNode(idx: Int): Pair<Node, Int> {
            val children = mutableListOf<Node>()
            var currentIdx = idx + 2
            for (i in 0 until input[idx]) {
                val subNode = buildNode(currentIdx)
                children.add(subNode.first)
                currentIdx = subNode.second
            }
            val meta = input.subList(currentIdx, currentIdx + input[idx + 1])
            return Pair(Node(children, meta), currentIdx + input[idx + 1])
        }

        return buildNode(0).first
    }

    private data class Node(val children: List<Node>, val meta: List<Int>) {
        fun sumMeta(): Int = meta.sum() + children.sumOf { it.sumMeta() }

        fun nodeValue(): Int  {
            if (children.isEmpty()) return sumMeta()
            return meta.filter { it > 0 && it <= children.size }.sumOf { children[it - 1].nodeValue() }
        }
    }
}