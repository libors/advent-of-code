package cz.libors.aoc.aoc22

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day(name = "Rock Paper Scissors")
object Day2 {

    enum class Hand(val idx: Int, val score: Array<Int>) {
        ROCK(0, arrayOf(0, -1, 1)),
        PAPER(1, arrayOf(1, 0, -1)),
        SCISSORS(2, arrayOf(-1, 1, 0));
    }

    private fun handScore(my: Hand, his: Hand) = my.idx + 1 + (my.score[his.idx] + 1) * 3

    private fun
            roundStrategy(his: Hand, matchResult: Int): Hand {
        val myHandIdx = his.score.toList().indexOf(-matchResult)
        return handFrom(myHandIdx)
    }

    private fun handFrom(idx: Int) = when(idx) {
        0 -> Hand.ROCK
        1 -> Hand.PAPER
        2 -> Hand.SCISSORS
        else -> throw RuntimeException("Unknown hand symbol: $idx")
    }

    private fun handFrom(x: Char) = when(x) {
        'A', 'X' -> Hand.ROCK
        'B', 'Y' -> Hand.PAPER
        'C', 'Z' -> Hand.SCISSORS
        else -> throw java.lang.RuntimeException("Unknown hand symbol: $x")
    }

    private fun task1(input: List<String>) = input.sumOf { handScore(handFrom(it[2]), handFrom(it[0])) }

    private fun task2(input: List<String>) = input
        .sumOf { handScore(roundStrategy(handFrom(it[0]), it[2] - 'X' - 1), handFrom(it[0])) }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input2.txt")
        println(task1(input))
        println(task2(input))
    }
}