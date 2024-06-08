package cz.libors.aoc.aoc23

import cz.libors.util.*
import java.math.BigDecimal
import kotlin.math.sign

@Day(name = "Never Tell Me The Odds")
object Day24 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input24.txt")
            .map { it.findLongs().map { l -> l.toBigDecimal() } }
        println(task1(input))
        TODO("Don't know how to do that, yet")
    }

    private fun task1(input: List<List<BigDecimal>>): Int {
        val min = BigDecimal(200000000000000)
        val max = BigDecimal(400000000000000)
        var cnt = 0
        for (i in input.indices) {
            for (j in i + 1 until input.size) {
                val a = input[i]
                val b = input[j]
                val inter = intersection(a, b)
                if (inter != null
                    && inter.first >= min && inter.first <= max
                    && inter.second >= min && inter.second <= max
                    && isFuture(a, inter) && isFuture(b, inter)
                ) cnt++
            }
        }
        return cnt
    }

    private fun task2() = "too hard, don't know"

    private fun isFuture(l: List<BigDecimal>, p: Pair<BigDecimal, BigDecimal>) =
        sign(l[3].toDouble()) == sign(p.first.toDouble() - l[0].toDouble())
                && sign(l[4].toDouble()) == sign(p.second.toDouble() - l[1].toDouble())

    private fun intersection(l1: List<BigDecimal>, l2: List<BigDecimal>): Pair<BigDecimal, BigDecimal>? {
        val (x1, y1) = Pair(l1[0], l1[1])
        val (x2, y2) = Pair(x1 + l1[3], y1 + l1[4])
        val (x3, y3) = Pair(l2[0], l2[1])
        val (x4, y4) = Pair(x3 + l2[3], y3 + l2[4])

        val denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
        if (denominator == BigDecimal.ZERO) return null
        val xNumerator = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)
        val yNumerator = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)

        return Pair(xNumerator / denominator, yNumerator / denominator)
    }
}