package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.permute
import cz.libors.util.readToLines
import cz.libors.util.set

@Day("Seven Segment Search")
object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input8.txt")
        println(task1(input))
        println(task2(input))
    }

    private val numSet = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")
        .map { it.toCharArray().fold( 0) { acc, ch -> acc.set(ch - 'a') } }

    private fun task1(input: List<String>) = input.sumOf {
        it.substringAfter("|")
            .split(" ")
            .map { x -> x.trim() }
            .count { x -> x.length == 2 || x.length == 3 || x.length == 4 || x.length == 7 }
    }

    private fun task2(input: List<String>): Int {
        val displays = input.map {
            val split = it.split(" | ")
            Display(split[0].split(Regex("\\s+")), split[1].split(Regex("\\s+")))
        }
        var sum = 0
        for (display in displays) {
            val perm = permute(listOf(0, 1, 2, 3, 4, 5, 6)) { check(display.wires, it) }
            val number = display.digits.joinToString("") { digit ->
                val num = digit.toCharArray().fold(0) { acc, d -> acc.set(perm[d - 'a']) }
                numSet.indexOf(num).toString()
            }.toInt()
            sum += number
        }
        return sum
    }

    private fun check(wires: List<String>, permutation: List<Int>): Boolean {
        for (w in wires) {
            var num = 0
            for (ch in w) num = num.set(permutation[ch - 'a'])
            if (!numSet.contains(num)) return false
        }
        return true
    }

    private data class Display(val wires: List<String>, val digits: List<String>)
}