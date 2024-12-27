package cz.libors.aoc.aoc20

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Encoding Error")
object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input9.txt").map { it.toLong() }
        val invalid = task1(input)
        println(invalid)
        println(task2(input, invalid))
    }

    private fun task1(input: List<Long>): Long {
        for (i in 25 until input.size) {
            if (!findAddition(i, input)) {
                return input[i]
            }
        }
        throw IllegalArgumentException()
    }

    private fun task2(input: List<Long>, invalid: Long): Long {
        for (i in input.indices) {
            var acc = input[i]
            var j = i
            while (acc < invalid) {
                acc += input[++j]
            }
            if (acc == invalid) {
                return (i..j).minOf { input[it] } + (i..j).maxOf { input[it] }
            }
        }
        throw IllegalArgumentException()
    }

    private fun findAddition(x: Int, input: List<Long>): Boolean {
        val expected = input[x]
        for (i in x - 25 until x) {
            for (j in i + 1 until x) {
                if (input[i] + input[j] == expected) return true
            }
        }
        return false
    }
}