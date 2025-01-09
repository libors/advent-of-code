package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.readToText

@Day("Infinite Elves and Infinite Houses")
object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input20.txt").toInt()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(target: Int): Int {
        val max = target / 10
        val a = IntArray(max)
        for (i in 1 until max) {
            for (j in i until max step i) {
                a[j] += i * 10
            }
        }
        for (i in 1 until max) if (a[i] >= target) return i
        throw IllegalStateException("max is too low")
    }

    private fun task2(target: Int): Int {
        val max = target / 10
        val a = IntArray(max)
        for (i in 1 until max) {
            for (n in 0 until 50) {
                val j = i + n * i
                if (j < max - 1) a[j] += i * 11
            }
        }
        for (i in 1 until max) if (a[i] >= target) return i
        throw IllegalStateException("max is too low")
    }
}