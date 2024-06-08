package cz.libors.aoc.aoc22

import cz.libors.util.Day
import cz.libors.util.readToLines
import java.lang.IllegalArgumentException
import kotlin.math.absoluteValue
import kotlin.math.pow

@Day(name = "Full of Hot Air")
object Day25 {

    private val reachableTable = reachableTable()

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input25.txt")
        val sum = input.sumOf { snafuToDec(it) }
        println(decToSnafu(sum))
    }

    private fun snafuToDec(num: String): Long {
        var result = 0L
        for (i in num.indices)
            result += 5.0.pow(i.toDouble()).toLong() * toDigit(num[num.length - 1 - i])
        return result
    }

    private fun reachableTable(): List<Pair<Part, Long>> {
        val result = mutableListOf<Pair<Part,Long>>()
        result.add(Pair(Part(0, 1), 1L))
        result.add(Pair(Part(0, 2), 2L))
        for (pow in 1..20) {
            val lastSum = result.last().second
            val one = Part(pow, 1)
            val two = Part(pow, 2)
            result.add(Pair(one, lastSum + one.value()))
            result.add(Pair(two, lastSum + two.value()))
        }
        return result
    }

    private fun decToSnafu(num: Long): String {
        var x = num
        val parts = mutableListOf<Part>()
        while (x != 0L) {
            val largest = findLargest(x)
            parts.add(largest)
            x -= largest.value()
        }
        val max = parts.maxOf { it.pow }
        val pows = parts.map { it.pow }.toSet()
        for (i in 0 until max) {
            if (!pows.contains(i)) {
                parts.add(Part(i, 0))
            }
        }
        return parts.sortedWith{ a, b -> b.pow - a.pow }
            .map { it.snafuDigit() }
            .toCharArray().concatToString()
    }

    private fun findLargest(x: Long): Part {
        val abs = x.absoluteValue
        val reach = reachableTable.first { it.second >= abs }
        return if ( x >= 0 ) reach.first else Part(reach.first.pow, -reach.first.digit)
    }

    private data class Part(val pow: Int, val digit: Int) {
        fun value() = (5.0.pow(pow) * digit).toLong()
        fun snafuDigit() = when(digit) {
            -2 -> '='
            -1 -> '-'
            else -> digit.toString()[0]
        }
    }

    private fun toDigit(ch: Char) = when (ch) {
        '1' -> 1L
        '2' -> 2L
        '0' -> 0L
        '-' -> -1L
        '=' -> -2L
        else -> throw IllegalArgumentException("Unsupported char: $ch")
    }
}