package cz.libors.aoc.aoc23

import cz.libors.util.*

@Day(name = "Haunted Wasteland")
object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input8.txt").splitByEmptyLine()
        val instructions = input[0]
        val rules = input[1].splitByNewLine().associate { line ->
            val split = line.findAlphanums()
            split[0] to Pair(split[1], split[2])
        }
        println(task1(rules, instructions))
        println(task2(rules, instructions))
    }

    private fun task1(rules: Map<String, Pair<String, String>>, instructions: String) =
        iterationsNum("AAA", { it == "ZZZ"}, rules, instructions)

    private fun task2(rules: Map<String, Pair<String, String>>, instructions: String): Long {
        val states = rules.entries.filter { it.key.endsWith("A") }.map { it.key }
        return states.map { state -> iterationsNum(state, { x -> x.endsWith("Z")}, rules, instructions) }
            .map { it.toLong() }
            .reduce { a, b -> lcm(a, b) }
    }

    private fun iterationsNum(start: String, end: (String) -> Boolean, rules: Map<String, Pair<String, String>>, instructions: String): Int {
        var pos = 0
        var state = rules[start]!!
        var cnt = 0
        do {
            val instruction = instructions[pos.mod(instructions.length)]
            val goto = if (instruction == 'L') state.first else state.second
            state = rules[goto]!!
            pos++
            cnt++
            if (end(goto)) {
                return cnt
            }
        } while (true)
    }
}