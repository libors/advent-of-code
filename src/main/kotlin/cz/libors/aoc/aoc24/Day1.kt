package cz.libors.aoc.aoc24

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines
import kotlin.math.abs

@Day("Historian Hysteria")
object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input1.txt").map { it.findInts() }
        val l1 = input.map { it.first() }.sorted()
        val l2 = input.map { it.last() }.sorted()

        println(task1(l1, l2))
        println(task2(l1, l2))
    }

    fun task1(l1: List<Int>, l2: List<Int>) = l1.zip(l2).sumOf { abs(it.first - it.second) }
    fun task2(l1: List<Int>, l2: List<Int>) = l1.sumOf { first -> first * l2.count { first == it } }
}