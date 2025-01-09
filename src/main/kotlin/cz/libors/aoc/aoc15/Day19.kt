package cz.libors.aoc.aoc15

import cz.libors.util.*

object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input19.txt").splitByEmptyLine()
        val transitions = createTransitions(input[0].splitByNewLine())
        val formula = splitToElements(input[1])
        println(task1(transitions, formula))
        println(task2(transitions, formula))
    }

    private fun task1(transitions: Map<String, List<List<String>>>, formula: List<String>): Int {
        val result = mutableSetOf<String>()
        for (pos in formula.indices) {
            val trList = transitions[formula[pos]]?:listOf()
            for (tr in trList) {
                result.add(replace(formula, pos, tr.joinToString("")))
            }
        }
        return result.size
    }

    private fun replace(input: List<String>, pos: Int, with: String) = input.mapIndexed { idx, v ->
        if (idx == pos) with else v
    }.joinToString("")

    private fun createTransitions(splitByNewLine: List<String>): Map<String, List<List<String>>> {
        val result = multiMap<String, List<String>>()
        for (line in splitByNewLine) {
            val input = line.substringBefore(" =>")
            val output = line.substringAfter("=> ").let { str -> splitToElements(str) }
            result.add(input, output)
        }
        return result
    }

    private fun task2(transitions: Map<String, List<List<String>>>, formula: List<String>): Int {
        TODO("needs analyzing input to get insights")
    }

    private fun splitToElements(str: String)= str.mapIndexedNotNull { idx, ch ->
        if (ch.isLowerCase()) null else {
            if (idx < str.length - 1 && str[idx + 1].isLowerCase()) {
                ch.toString() + str[idx + 1]
            } else ch.toString()
        }
    }
}