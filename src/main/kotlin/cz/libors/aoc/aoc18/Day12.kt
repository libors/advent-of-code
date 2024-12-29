package cz.libors.aoc.aoc18

import cz.libors.util.Day
import cz.libors.util.readToText
import cz.libors.util.splitByEmptyLine
import cz.libors.util.splitByNewLine
import kotlin.math.max

@Day("Subterranean Sustainability")
object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input12.txt").splitByEmptyLine()
        val pots = input[0].substringAfter(": ")
        val rules = input[1].splitByNewLine().associate { it.substring(0, 5) to it[9] }
        println(task1(pots, rules))
        println(task2(pots, rules))
    }

    private fun task1(pots: String, rules: Map<String, Char>) = countAfterGenerations(pots, rules, 20)
    private fun task2(pots: String, rules: Map<String, Char>) = countAfterGenerations(pots, rules, 50000000000)

    private fun countAfterGenerations(pots: String, rules: Map<String, Char>, generations: Long): Long {
        var generation = pots
        var firstPotNum = 0L
        var prev: Pair<String, Long>

        for (i in 1..generations) {
            prev = Pair(generation, firstPotNum)
            val normalizeResult = normalizeEnds(generation)
            generation = normalizeResult.first
            firstPotNum -= normalizeResult.second
            generation = updateGeneration(generation, rules)
            firstPotNum += 4
            if (prev.first == generation) {
                val remainingRuns = generations - i
                val firstPotNumDiff = firstPotNum - prev.second
                return sumPots(generation, firstPotNum + remainingRuns * firstPotNumDiff)
            }
        }
        return sumPots(generation, firstPotNum)
    }

    private fun sumPots(pots: String, first: Long) = pots.mapIndexed { idx, c -> if (c == '.') 0 else idx + first }.sum()

    private fun updateGeneration(generation: String, rules: Map<String, Char>): String {
        val sb = StringBuilder()
        for (i in 2..generation.length - 5) {
            val result = rules[generation.substring(i, i + 5)]
            sb.append(if (result == '#') '#' else '.')
        }
        return sb.toString()
    }

    private fun normalizeEnds(generation: String): Pair<String, Int> {
        val sb = StringBuilder()
        val dotsStart = generation.substringBefore('#').length
        val dotsEnd = generation.substringAfterLast('#').length
        val appendStartDots = max(0, 5 - dotsStart)
        for (i in 1..appendStartDots) sb.append(".")
        sb.append(generation)
        for (i in dotsEnd..5) sb.append(".")
        return Pair(sb.toString(), appendStartDots)
    }
}