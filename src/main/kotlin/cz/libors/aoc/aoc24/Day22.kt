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

    private fun task1(secretNums: List<Long>) = secretNums.sumOf {
        var a = it
        repeat(DERIVE_REPEAT) { a = deriveNext(a) }
        a
    }

    private fun task2(secretNums: List<Long>): Int {
        val sums = IntArray(SEQUENCES_MAX_SIZE)
        secretNums.forEach { updateSumsForNum(it, sums) }
        return sums.max()
    }

    private data class PriceChange(val price: Int, val change: Int)

    private fun updateSumsForNum(secretNum: Long, sums: IntArray) {
        val used = BooleanArray(SEQUENCES_MAX_SIZE)
        val changes = mutableListOf<PriceChange>()
        var currentSecretNum = secretNum
        var prevPrice = lastDigit(currentSecretNum)
        for (i in 1..DERIVE_REPEAT) {
            currentSecretNum = deriveNext(currentSecretNum)
            val price = lastDigit(currentSecretNum)
            changes.add(PriceChange(price, price - prevPrice))
            prevPrice = price
        }
        for (i in 3 until DERIVE_REPEAT) {
            val idx = arrayIdx(changes[i - 3].change, changes[i - 2].change, changes[i - 1].change, changes[i].change)
            if (!used[idx]) {
                sums[idx] += changes[i].price
                used[idx] = true
            }
        }
    }

    private fun arrayIdx(a: Int, b: Int, c: Int, d: Int) = a + 9 + (b + 9) * PRICE_DIFF_SPAN +
            (c + 9) * PRICE_DIFF_SPAN * PRICE_DIFF_SPAN +
            (d + 9) * PRICE_DIFF_SPAN * PRICE_DIFF_SPAN * PRICE_DIFF_SPAN

    private fun lastDigit(a: Long) = (a % 10).toInt()

    private fun deriveNext(secretNum: Long): Long {
        var res = (secretNum * 64) xor secretNum
        res %= MOD_VALUE
        res = (res / 32) xor res
        res %= MOD_VALUE
        res = (res * 2048) xor res
        res %= MOD_VALUE
        return res
    }
}