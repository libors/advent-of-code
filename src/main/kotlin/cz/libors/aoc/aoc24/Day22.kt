package cz.libors.aoc.aoc24

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Monkey Market")
object Day22 {

    private const val MOD_VALUE = 16777216
    private const val DERIVE_REPEAT = 2000

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input22.txt").map { it.toLong() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Long>) = input.sumOf {
        var a = it
        repeat(DERIVE_REPEAT) { a = derive(a) }
        a
    }

    private fun task2(input: List<Long>): Int {
        val maps = input.map { priceChangeSequences(it) }
        val keys = maps.flatMap { it.keys }.distinct()
        return keys.maxOf { key -> maps.sumOf { it[key] ?: 0 } }
    }

    private data class PriceChange(val price: Int, val change: Int)

    private fun priceChangeSequences(x: Long): Map<List<Int>, Int> {
        val result = mutableMapOf<List<Int>, Int>()
        val changes = mutableListOf<PriceChange>()
        var a = x
        var prev = lastDigit(a)
        for (i in 1..DERIVE_REPEAT) {
            a = derive(a)
            val price = lastDigit(a)
            changes.add(PriceChange(price, price - prev))
            prev = price
        }
        for (i in 3 until DERIVE_REPEAT) {
            val seq = listOf(changes[i - 3].change, changes[i - 2].change, changes[i - 1].change, changes[i].change)
            if (!result.containsKey(seq)) result[seq] = changes[i].price
        }
        return result
    }

    private fun lastDigit(a: Long) = (a % 10).toInt()

    private fun derive(x: Long): Long {
        var res = (x * 64) xor x
        res %= MOD_VALUE
        res = (res / 32) xor res
        res %= MOD_VALUE
        res = (res * 2048) xor res
        res %= MOD_VALUE
        return res
    }
}