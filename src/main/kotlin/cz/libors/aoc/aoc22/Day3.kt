package cz.libors.aoc.aoc22

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day(name = "Rucksack Reorganization")
object Day3 {

    private fun commonChar(strings: List<CharSequence>) =
        strings.map { it.toSet() }
            .reduce {s1, s2 -> s1.intersect(s2)}
            .iterator().next()

    private fun charPriority(ch: Char) = if (ch.isLowerCase()) ch - 'a' + 1 else ch - 'A' + 27

    private fun task1(input: List<String>) = input
        .map { commonChar(listOf(it.subSequence(0, it.length / 2), it.subSequence(it.length / 2, it.length))) }
        .sumOf { charPriority(it) }

    private fun task2(input: List<String>) = input.chunked(3)
        .map { commonChar(it) }
        .sumOf { charPriority(it) }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input3.txt")
        println(task1(input))
        println(task2(input))
    }
}