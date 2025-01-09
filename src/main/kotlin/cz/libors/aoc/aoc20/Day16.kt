package cz.libors.aoc.aoc20

import cz.libors.util.*

@Day("Ticket Translation")
object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input16.txt").splitByEmptyLine()
        val rules = input[0].splitByNewLine().associate {
            val nums = it.findPositiveInts()
            Pair(it.substringBefore(":"), listOf(nums[0]..nums[1], nums[2]..nums[3]))
        }
        val myTicket = input[1].splitByNewLine()[1].findInts()
        val otherTickets = input[2].splitByNewLine().drop(1).map { it.findInts() }

        println(task1(rules, otherTickets))
        println(task2(rules, myTicket, otherTickets))
    }

    private fun task2(rules: Map<String, List<IntRange>>, myTicket: List<Int>, otherTickets: List<List<Int>>): Long {
        val correctTickets = otherTickets.filter { t -> t.all { matches(it, rules).isNotEmpty() } }
        val mapping = Array(myTicket.size) { mutableMapOf<String, Int>() }
        for (ticket in correctTickets) {
            for (i in ticket.indices) {
                matches(ticket[i], rules).forEach { mapping[i].merge(it, 1, Int::plus) }
            }
        }
        val assigned = assignFields(mapping.map { it.toMap() }.toTypedArray(), correctTickets)

        return assigned.filter { it.key.startsWith("departure") }
            .mapValues { myTicket[it.value] }
            .values.fold(1L) { a, b -> a * b }
    }

    private fun assignFields(mapping: Array<Map<String, Int>>, correctTickets: List<List<Int>>): Map<String, Int> {
        val toProcess = mapping.map { idx -> idx.filter { it.value == correctTickets.size }.map { it.key } }
            .mapIndexed { idx, v -> Pair(idx, v.toMutableSet()) }.toMap().toMutableMap()
        val assigned = mutableMapOf<String, Int>()
        while (toProcess.isNotEmpty()) {
            val single = toProcess.filter { it.value.size == 1 }
            for (s in single) {
                assigned[s.value.single()] = s.key
                toProcess.remove(s.key)
                toProcess.values.forEach { it.remove(s.value.single()) }
            }
        }
        return assigned
    }

    private fun task1(rules: Map<String, List<IntRange>>, otherTickets: List<List<Int>>) =
        otherTickets.flatten().filter { matches(it, rules).isEmpty() }.sum()

    private fun matches(num: Int, rules: Map<String, List<IntRange>>): List<String> {
        val result = mutableListOf<String>()
        for (cat in rules) {
            for (range in cat.value) {
                if (range.contains(num)) {
                    result.add(cat.key)
                    break
                }
            }
        }
        return result
    }
}