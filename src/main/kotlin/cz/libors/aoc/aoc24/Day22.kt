package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("Monkey Market")
object Day22 {

    private const val MOD_VALUE = 16777216
    private const val DERIVE_REPEAT = 2000
    private const val PRICE_DIFF_SPAN = 19
    private const val SEQUENCES_MAX_SIZE = PRICE_DIFF_SPAN * PRICE_DIFF_SPAN * PRICE_DIFF_SPAN * PRICE_DIFF_SPAN

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
        return (0 until SEQUENCES_MAX_SIZE)
            .maxOf { idx -> maps.sumOf { val a = it[idx].toInt(); if (a == -1) 0 else a } }
    }

    private data class PriceChange(val price: Int, val change: Int)

    private fun priceChangeSequences(x: Long): ByteArray {
        val result = ByteArray(SEQUENCES_MAX_SIZE) { -1 }
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
            val idx = arrayIdx(changes[i - 3].change, changes[i - 2].change, changes[i - 1].change, changes[i].change)
            if (result[idx] == (-1).toByte()) result[idx] = changes[i].price.toByte()
        }
        return result
    }

    private fun arrayIdx(a: Int, b: Int, c: Int, d: Int) = a + 9 + (b + 9) * PRICE_DIFF_SPAN +
            (c + 9) * PRICE_DIFF_SPAN * PRICE_DIFF_SPAN +
            (d + 9) * PRICE_DIFF_SPAN * PRICE_DIFF_SPAN * PRICE_DIFF_SPAN

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