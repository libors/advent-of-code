package cz.libors.aoc.aoc20

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Adapter Array")
object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input10.txt").map { it.toInt() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Int>): Int {
        val diffs = (input + 0).sorted().zipWithNext().map { it.second - it.first }.groupBy { it }
        return diffs[1]!!.size * (diffs[3]!!.size + 1)
    }

    private fun task2(input: List<Int>): Long {
        val adapters = (input + 0).sorted()
        val memo = mutableMapOf<Int, Long>()

        fun countArrangements(idx: Int): Long {
            val fromMemo = memo[idx]
            if (fromMemo != null) return fromMemo
            val v = adapters[idx]
            val options = mutableListOf<Int>()
            for (i in idx + 1 until adapters.size) {
                if (adapters[i] <= v + 3) options.add(i) else break
            }
            if (options.size == 0) return 1
            val result = options.sumOf { countArrangements(it) }
            memo[idx] = result
            return result
        }

        return countArrangements(0)
    }
}