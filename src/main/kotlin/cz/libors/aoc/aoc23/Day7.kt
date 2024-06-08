package cz.libors.aoc.aoc23

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day(name = "Camel Cards")
object Day7 {

    private const val CARDS = "AKQJT98765432"
    private const val JOKER_CARDS = "AKQT98765432J"

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input7.txt")
            .map { it.split(" ") }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<List<String>>) = computeGamesSum(input.map { Game(it[0], it[1].toInt(), false) })
    private fun task2(input: List<List<String>>) = computeGamesSum(input.map { Game(it[0], it[1].toInt(), true) })

    private fun computeGamesSum(input: List<Game>): Int {
        val comparator = compareBy<Game> ({ it.typeStrength }, { it.cardStrengths[0] },
            { it.cardStrengths[1] }, { it.cardStrengths[2] }, { it.cardStrengths[3] }, { it.cardStrengths[4] })
        return input.sortedWith(comparator).mapIndexed { idx, game -> (idx + 1) * game.bid}.sum()
    }

    private class Game(val hand: String, val bid: Int, jokersEnabled: Boolean) {
        val typeStrength = 10 - type(jokersEnabled)
        val cardStrengths = hand.toCharArray().map { 20 - if (jokersEnabled) JOKER_CARDS.indexOf(it ) else CARDS.indexOf(it) }

        fun type(jokersEnabled: Boolean): Int {
            val jokers = if (jokersEnabled) hand.count { it == 'J' } else 0
            val cards = hand.toCharArray().toList()
                .filter { !jokersEnabled || it != 'J' }
                .groupingBy { it }.eachCount().values.sorted().reversed()
            val best = if (cards.isEmpty()) jokers else cards[0] + jokers
            return when {
                best == 5 -> 1
                best == 4 -> 2
                best == 3 && cards[1] == 2 -> 3
                best == 3 -> 4
                best == 2 && cards[1] == 2 -> 5
                best == 2 -> 6
                else -> 7
            }
        }
    }
}