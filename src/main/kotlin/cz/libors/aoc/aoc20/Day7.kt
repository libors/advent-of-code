package cz.libors.aoc.aoc20

import cz.libors.util.Day
import cz.libors.util.bfsToAll
import cz.libors.util.readToLines

@Day("Handy Haversacks")
object Day7 {

    private val fullRegex = Regex("^(.*) bags contain (.*)$")
    private val partRegex = Regex("(\\d+) (.*) bags?\\.?")

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input7.txt").map {
            val (what, parts) = fullRegex.matchEntire(it)!!.destructured
            Rule(what, map(parts))
        }
        println(task1(input))
        println(task2(input))
    }

    private fun map(parts: String) = parts.split(", ")
        .mapNotNull { part ->
            if (part == "no other bags.") null else {
                partRegex.matchEntire(part)!!.groupValues.let { p -> p[2] to p[1].toInt() }
            }
        }.toMap()

    private fun task2(input: List<Rule>): Int {
        val map = input.associate { it.bag to it.contain }

        fun dfs(x: String): Int =
            map[x]!!.let { c -> if (c.isEmpty()) 1 else 1 + c.entries.sumOf { it.value * dfs(it.key) } }

        return dfs("shiny gold") - 1
    }

    private fun task1(input: List<Rule>) =
        bfsToAll("shiny gold") { x -> input.filter { it.contain.containsKey(x) }.map { it.bag } }
            .distances().size - 1


    private data class Rule(val bag: String, val contain: Map<String, Int>)
}