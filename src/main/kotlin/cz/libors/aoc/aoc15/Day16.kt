package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines
import cz.libors.util.splitByNewLine

private typealias ItemList = Map<String, Int>

@Day("Aunt Sue")
object Day16 {

    val giftContent = """
        children: 3
        cats: 7
        samoyeds: 2
        pomeranians: 3
        akitas: 0
        vizslas: 0
        goldfish: 5
        trees: 3
        cars: 2
        perfumes: 1
    """.trimIndent()

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input16.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>) = goThruAuntList(input, ::matchGift)
    private fun task2(input: List<String>) = goThruAuntList(input, ::matchIntervals)

    private fun goThruAuntList(input: List<String>, matchFn: (ItemList, ItemList) -> Boolean): Int {
        val gift = giftContent.splitByNewLine().map { it.substringBefore(':') to it.findInts()[0] }
            .filter { it.second != 0 }.toMap()
        for (line in input) {
            val (id, map) = convertLine(line)
            if (matchFn(gift, map)) return id
        }
        throw IllegalArgumentException()
    }

    private fun convertLine(s: String): Pair<Int, Map<String, Int>> {
        val sueNum = s.findInts()[0]
        val map = s.substringAfter(": ").split(", ")
                .associate { item -> item.split(": ").let { it[0] to it[1].toInt() } }
        return sueNum to map
    }

    private fun matchGift(beeped: ItemList, aunt: ItemList) = aunt.all { (k, v) -> beeped[k] == v }

    private fun matchIntervals(beeped: ItemList, aunt: ItemList) = aunt.all { (k, v) ->
        when (k) {
            "cats", "trees" -> beeped[k] != null && beeped[k]!! < v
            "pomeranians", "goldfish" -> beeped[k] != null && beeped[k]!! > v
            else -> beeped[k] == v
        }
    }
}