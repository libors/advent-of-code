package cz.libors.aoc.aoc23

import cz.libors.util.*

private typealias Part = List<Int>

@Day(name = "Aplenty")
object Day19 {

    private const val CATEGORIES = "xmas"

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input19.txt").splitByEmptyLine()
        val workflows = input[0].splitByNewLine().map { parseWorkflow(it) }.associateBy { it.id }
        val parts = input[1].splitByNewLine().map { it.findInts() }

        println(task1(parts, workflows))
        println(task2(workflows))
    }

    private fun task1(parts: List<Part>, workflows: Map<String, Workflow>) = parts
        .filter { checkPart(workflows, it) }
        .sumOf { it.sum() }

    private fun task2(workflows: Map<String, Workflow>) = computeOptions(workflows, "in", listOf())

    private fun computeOptions(wfs: Map<String, Workflow>,
                               target: String,
                               rules: List<Pair<Rule, Boolean>>):Long = when(target) {
        "A" -> productOfOptions(rules)
        "R" -> 0
        else -> {
            var result = 0L
            val wf = wfs[target]!!
            val negativeRules = mutableListOf<Pair<Rule, Boolean>>()
            for (rule in wf.rules) {
                result += computeOptions(wfs, rule.output, rules + negativeRules + listOf(Pair(rule, false)))
                negativeRules.add(Pair(rule, true))
            }
            result
        }
    }

    private fun productOfOptions(rules: List<Pair<Rule, Boolean>>): Long {
        val map = rules
            .filter { it.first.op != '-' }
            .map { item ->
                item.first.what to if (item.second) { x: Int -> !item.first.predicate().invoke(x) }
                else item.first.predicate()
            }
            .groupBy { it.first }
        val partials = map.values.map { char ->
            (1..4000).count { num -> char.all { c -> c.second(num) } }
        }
        var result = partials.fold(1L) { a, b -> a * b }
        for (i in 1..(4 - partials.size)) {
            result *= 4000
        }
        return result
    }

    private fun checkPart(wfs: Map<String, Workflow>, part: Part): Boolean {
        var wf = wfs["in"]!!
        do {
            when (val outcome = wf.send(part)) {
                "A" -> return true
                "R" -> return false
                else -> wf = wfs[outcome]!!
            }
        } while (true)
    }

    private fun parseWorkflow(it: String): Workflow {
        // px{a<2006:qkq,m>2090:A,rfg}
        val id = it.substringBefore("{")
        val rules = it.substringAfter("{").substringBefore("}").split(",").map {
            val split = it.findAlphanums()
            if (split.size == 1) {
                Rule('-', '-', 0, split[0])
            } else {
                val op = if (it.contains('>')) '>' else '<'
                Rule(split[0][0], op, split[1].toInt(), split[2])
            }
        }
        return Workflow(id, rules)
    }

    private data class Rule(val what: Char, val op: Char, val value: Int, val output: String) {
        fun predicate(): (Int) -> Boolean = when (op) {
            '>' -> { x -> x > value }
            '<' -> { x -> x < value }
            else -> { _ -> true }
        }

        fun evaluate(part: Part): String? = when (op) {
            '>' -> if (part[CATEGORIES.indexOf(what)] > value) output else null
            '<' -> if (part[CATEGORIES.indexOf(what)] < value) output else null
            else -> output
        }
    }

    private data class Workflow(val id: String, val rules: List<Rule>) {
        fun send(part: Part): String {
            for (rule in rules) {
                val result = rule.evaluate(part)
                if (result != null) return result
            }
            throw IllegalStateException("No rule matched")
        }
    }
}
