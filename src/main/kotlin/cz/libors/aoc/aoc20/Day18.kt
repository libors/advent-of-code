package cz.libors.aoc.aoc20

import cz.libors.util.Day
import cz.libors.util.readToLines
import java.util.*

@Day("Operation Order")
object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input18.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>) = input.sumOf { evaluate(tokenize(it)) }
    private fun task2(input: List<String>) = input.sumOf { evaluate(prioritizePlus(tokenize(it))) }

    // not proud of this, overcomplicated. adding () to prioritize plus and leverage evaluator from part 1
    private fun prioritizePlus(tokens: List<String>): List<String> {
        val result = mutableListOf<String>()
        var inPlus = false
        val stack = LinkedList<Boolean>()
        for (i in tokens.indices) {
            val token = tokens[i]
            if (token == "(") {
                if (!inPlus) result.add("(")
                stack.push(inPlus)
                inPlus = false
            } else if (token == ")") {
                if (inPlus) result.add(")")
                inPlus = stack.pop()
                if (!inPlus) {
                    if (i < tokens.size - 1 && tokens[i+1] == "+") inPlus = true else result.add(")")
                }
            } else if (!inPlus && token.toIntOrNull() != null && i < tokens.size - 1 && tokens[i + 1] == "+") {
                inPlus = true
                result.add("(")
            } else if (inPlus && "*".contains(token)) {
                inPlus = false
                result.add(")")
            }
            result.add(token)
        }
        if (inPlus) result.add(")")
        return result
    }

    private fun tokenize(s: String) = s.replace("(", " ( ").replace(")", " ) ")
        .trim().split(Regex("\\s+"))

    private fun evaluate(tokens: List<String>): Long {
        var sum = 0L
        var op = '+'
        val stack = LinkedList<Pair<Long, Char>>()
        for (token in tokens) {
            if ("+*".contains(token)) {
                op = token[0]
            } else if (token == "(") {
                stack.push(sum to op)
                sum = 0
                op = '+'
            } else if (token == ")") {
                val (ssum, sop) = stack.pop()
                sum = if (sop == '+') ssum + sum else ssum * sum
            } else {
                val num = token.toInt()
                sum = if (op == '+') sum + num else sum * num
            }
        }
        return sum
    }
}