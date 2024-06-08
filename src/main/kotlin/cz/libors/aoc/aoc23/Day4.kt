package cz.libors.aoc.aoc23

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines
import kotlin.math.pow

@Day(name = "Scratchcards")
object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input4.txt")
            .map {
                val parts = it.substringAfter(":").split("|")
                Card(parts[0].findInts(), parts[1].findInts())
            }

        println(task1(input))
        println(task2(input))
    }

    private fun task1(cards: List<Card>): Int {
        val winningNumbers = cards.map { c -> c.numbers.filter { n -> c.winning.contains(n) }.size }
        return winningNumbers.filter { it > 0 }.sumOf { 2.0.pow(it - 1.0) }.toInt()
    }

    private fun task2(cards: List<Card>): Int {
        val winningNumbers = cards.map { c -> c.numbers.filter { n -> c.winning.contains(n) }.size }
        return cards.indices.sumOf { cardsFor(it, winningNumbers) }
    }

    private fun cardsFor(card: Int, cards: List<Int>): Int =
        if (cards[card] == 0) 1
        else 1 + (card + 1 until card + 1 + cards[card]).sumOf { cardsFor(it, cards) }

    private data class Card(val winning: List<Int>, val numbers: List<Int>)
}