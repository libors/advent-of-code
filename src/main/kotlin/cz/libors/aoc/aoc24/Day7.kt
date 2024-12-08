package cz.libors.aoc.aoc24

import cz.libors.util.*
private typealias Operation = (Long, Long) -> Long

@Day("Bridge Repair")
object Day7 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input7.txt").map { it.findLongs() }
            .map { Pair(it[0], it.subList(1, it.size)) }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Pair<Long, List<Long>>>)= sumOf(input, listOf(
        {a, b -> a + b},
        {a, b -> a * b}))

    private fun task2(input: List<Pair<Long, List<Long>>>)= sumOf(input, listOf(
        {a, b -> a + b},
        {a, b -> a * b},
        {a, b -> (a.toString() + b.toString()).toLong()}))

    private fun sumOf(input: List<Pair<Long, List<Long>>>, ops: List<Operation>) = input
        .filter { compute(it.first, it.second, ops, 1, it.second[0]) }
        .sumOf { it.first }

    private fun compute(result: Long, nums: List<Long>, ops: List<Operation>, pos: Int, soFar: Long): Boolean = when {
        pos == nums.size -> soFar == result
        soFar > result -> false
        else -> ops.any { compute(result, nums, ops,pos + 1, it(soFar, + nums[pos])) }
    }
}