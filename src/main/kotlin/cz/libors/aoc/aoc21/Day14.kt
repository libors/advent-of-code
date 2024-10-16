package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.findAlphanums
import cz.libors.util.readToText
import cz.libors.util.splitByEmptyLine

@Day("Extended Polymerization")
object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input14.txt").splitByEmptyLine()
        val string = input[0]
        val rules = input[1].lines().map { it.findAlphanums() }.associate { it[0] to it[1] }
        println(task1(string, rules))
        println(task2(string, rules))
    }

    private fun task1(s: String, rules: Map<String, String>) = Expander(rules).expand(s, 10)
    private fun task2(s: String, rules: Map<String, String>) = Expander(rules).expand(s, 40)

    private class Expander(val rules: Map<String, String>) {
        private val memo = mutableMapOf<Pair<String, Int>, Map<String, Long>>()

        fun expand(s: String, steps: Int): Long {
            val pairMap = mutableMapOf<String, Long>()
            for (i in 0..s.length - 2) {
                val x = compute(s.substring(i, i + 2), steps)
                x.forEach { pairMap.merge(it.key, it.value, Long::plus) }
            }
            val charMap = mutableMapOf<Char, Long>()
            pairMap.forEach { (k, v) -> charMap.merge(k[0], v, Long::plus) }
            charMap.merge(s[s.length - 1], 1L, Long::plus)
            val sorted = charMap.values.sorted()
            return sorted.last() - sorted.first()
        }

        private fun compute(charPair: String, step: Int): Map<String, Long> {
            val memoKey = Pair(charPair, step)
            val cached = memo[memoKey]
            if (cached != null) return cached
            val result = if (step == 0) {
                 mapOf(charPair to 1L)
            } else {
                val expansion = rules[charPair]
                if (expansion == null)
                    mapOf(charPair to 1L)
                else {
                    val part1 = compute(charPair[0] + expansion, step - 1)
                    val part2 = compute(expansion + charPair[1], step - 1)
                    val result = part1.toMutableMap()
                    part2.forEach { result.merge(it.key, it.value, Long::plus) }
                    result
                }
            }
            memo[memoKey] = result
            return result
       }
    }
}