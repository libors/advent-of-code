package cz.libors.aoc.aoc24

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToText

@Day("Plutonian Pebbles")
object Day11 {

    private data class MemoKey(val stone: Long, val steps: Int)
    private val memo = mutableMapOf<MemoKey, Long>()

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input11.txt").findInts().map { it.toLong() }
        println(task1(input)).also { memo.clear() }
        println(task2(input)).also { memo.clear() }
    }

    private fun task1(stones: List<Long>) = stones.sumOf { countStones(it, 25) }
    private fun task2(stones: List<Long>) = stones.sumOf { countStones(it, 75) }

    private fun countStones(stone: Long, steps: Int): Long {
        if (steps == 0) return 1
        val memoKey = MemoKey(stone, steps)
        val fromMemo = memo[memoKey]
        if (fromMemo != null) return fromMemo
        val sum = transformStone(stone).sumOf { countStones(it, steps - 1) }
        memo[memoKey] = sum
        return sum
    }

    private fun transformStone(stone: Long): List<Long> {
        if (stone == 0L) return listOf(1L)
        val x = stone.toString()
        if (x.length % 2 == 0) {
            val center = x.length / 2
            return listOf(x.substring(0, center).toLong(), x.substring(center).toLong())
        }
        return listOf(stone * 2024)
    }
}