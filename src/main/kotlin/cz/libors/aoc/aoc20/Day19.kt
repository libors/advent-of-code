package cz.libors.aoc.aoc20

import cz.libors.util.*

@Day("Monster Messages")
object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input19.txt").splitByEmptyLine()
        val messages = input[1].splitByNewLine()
        val rules = input[0].splitByNewLine().map { toRule(it) }.sortedBy { it.id }.toTypedArray()
        println(task1(rules, messages))
        println(task2(rules, messages))
    }

    private fun task1(rules: Array<Rule>, messages: List<String>) = countMatches(rules, messages)

    private fun task2(rules: Array<Rule>, messages: List<String>): Int {
        rules[8] = Rule(8, null, listOf(42), listOf(42, 8))
        rules[11] = Rule(11, null, listOf(42, 31), listOf(42, 11, 31))
        return countMatches(rules, messages)
    }

    private fun countMatches(rules: Array<Rule>, messages: List<String>): Int {

        fun matches(str: String, ruleQueue: List<Int>, pos: Int): Boolean {
            if (ruleQueue.isEmpty()) return pos == str.length
            if (pos == str.length) return false
            val rule = rules[ruleQueue[0]]
            return if (rule.ch != null) {
                return str[pos] == rule.ch && matches(str, ruleQueue.drop(1), pos + 1)
            } else {
                matches(str, rule.o1 + ruleQueue.drop(1), pos) ||
                        (rule.o2.isNotEmpty()  && matches(str, rule.o2 + ruleQueue.drop(1), pos))
            }
        }

        return messages.count { matches(it, listOf(0), 0) }
    }

    private fun toRule(it: String): Rule {
        val id = it.substringBefore(":").toInt()
        return if (it.contains('"')) {
            Rule(id, it.substringAfter("\"")[0])
        } else if (it.contains('|')) {
            Rule(id, null, it.substringBefore("|").findInts().drop(1), it.substringAfter("|").findInts())
        } else {
            Rule(id, null, it.findInts().drop(1))
        }
    }

    private data class Rule(val id: Int, val ch: Char? = null, val o1: List<Int> = listOf(), val o2: List<Int> = listOf())
}