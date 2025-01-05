package cz.libors.aoc.aoc15

import cz.libors.util.Composite
import cz.libors.util.Day
import cz.libors.util.readToText
import java.util.*

@Day("JSAbacusFramework.io")
object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input12.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String): Int {
        return Regex("-?\\d+").findAll(input).sumOf { it.value.toInt() }
    }

    private fun task2(input: String): Int {
        val reds = Regex(":\"red\"").findAll(input).map { it.range.start }.toList()
        val nums = Regex("-?\\d+").findAll(input).map { it.range.start to it.value.toInt() }.toList()
        val nodes = LinkedList<Composite<Item>>()
        nodes.addLast(Composite(Item(true)))
        val redsIterator = reds.iterator()
        val numsIterator = nums.iterator()
        var curRed = redsIterator.next()
        var curNum = numsIterator.next()
        for (i in input.indices) {
            if (i == curRed) {
                nodes.last.item.red = true
                if (redsIterator.hasNext()) curRed = redsIterator.next()
            }
            if (i == curNum.first) {
                nodes.last.item.sum += curNum.second
                if (numsIterator.hasNext()) curNum = numsIterator.next()
            }
            when(input[i]) {
                '[', '{' -> {
                    val structure = Composite(Item(input[i] == '{'))
                    nodes.last.add(structure)
                    nodes.add(structure)
                }
                '}', ']' -> {
                    nodes.removeLast()
                }
            }
        }

        return sum(nodes.last)
    }

    private fun sum(c: Composite<Item>): Int = if (c.item.ignore()) 0 else c.item.sum + c.children.sumOf { sum(it) }

    private data class Item(var obj: Boolean, var sum: Int = 0, var red: Boolean = false) {
        fun ignore() = obj && red
    }
}