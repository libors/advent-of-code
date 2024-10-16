package cz.libors.aoc.aoc22

import cz.libors.util.*
import java.lang.Integer.min

@Day(name = "Distress Signal")
object Day13 {

    private fun task1(data: List<List<TreeNode<Int>>>) =
        data.mapIndexed { idx, it -> Pair(idx + 1, compare(it[0], it[1])) }
            .filter { it.second < 0 }
            .sumOf { it.first }

    private fun task2(data: List<List<TreeNode<Int>>>): Int {
        val d1 = TreeNode(items = listOf(TreeNode(v = 2)))
        val d2 = TreeNode(items = listOf(TreeNode(v = 6)))
        val sorted = data.flatten()
            .plus(listOf(d1, d2))
            .sortedWith { l, r -> compare(l, r).toInt() }
        return (sorted.indexOf(d1) + 1) * (sorted.indexOf(d2) + 1)
    }

    private fun compare(first: TreeNode<Int>, second: TreeNode<Int>): Int {
        if (first.isValue() && second.isValue())
            return first.v!! - second.v!!
        else if (first.isValue())
            return compare(TreeNode(items = listOf(first)), second)
        else if (second.isValue())
            return compare(first, TreeNode(items = listOf(second)))
        else {
            val left = first.items
            val right = second.items
            for (i in 0 until min(left.size, right.size)) {
                val cmp = compare(left[i], right[i])
                if (cmp != 0) return cmp
            }
            return left.size - right.size
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input13.txt")
            .splitByEmptyLine()
            .map { it.splitByNewLine().map { s -> s.readTree { x -> x.toInt() } } }
        println(task1(input))
        println(task2(input))
    }
}