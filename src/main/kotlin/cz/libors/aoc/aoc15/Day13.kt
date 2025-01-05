package cz.libors.aoc.aoc15

import cz.libors.util.*

@Day("Knights of the Dinner Table")
object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input13.txt").map { toRelation(it) }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(relations: List<Relation>) = findHappiness(relations)
    private fun task2(relations: List<Relation>) = findHappiness(relations + Relation("Me", "Me", 0))

    private fun findHappiness(relations: List<Relation>): Int {
        val people = (relations.map { it.sitting } + relations.map { it.nextTo }).distinct().toTypedArray()
        val map = relations.map { Pair(it.sitting, it.nextTo) to it.units }.toMap()
        return permute(people.size).maxOf { order -> happiness(order.map { people[it] }, map) }
    }

    private fun happiness(order: List<String>, map: Map<Pair<String, String>, Int>): Int {
        var sum = 0
        for (i in order.indices) {
            sum += map[Pair(order[i], order[(i + 1) % order.size])] ?: 0
            sum += map[Pair(order[i], order[(i - 1).posMod(order.size)])] ?: 0
        }
        return sum
    }

    private fun toRelation(it: String): Relation {
        val words = it.findAlphanums()
        val nums = it.findInts()
        val minus = words[2] == "lose"
        return Relation(words[0], words[words.size - 1], if (minus) -nums[0] else nums[0])
    }

    private data class Relation(val sitting: String, val nextTo: String, val units: Int)
}