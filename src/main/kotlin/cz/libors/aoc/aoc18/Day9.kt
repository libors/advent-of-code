package cz.libors.aoc.aoc18

import cz.libors.util.Circle
import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToText

@Day("Marble Mania")
object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input9.txt").findInts()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(params: List<Int>) = play(params[0], params[1])
    private fun task2(params: List<Int>) = play(params[0], params[1] * 100)

    private fun play(players: Int, maxRound: Int): Long {
        val circle = Circle<Int>()
        var player = 0
        val scores = LongArray(players)
        circle.add(0)
        for (round in 1..maxRound) {
            if (round % 23 == 0) {
                scores[player] += round.toLong()
                circle.move(-7)
                scores[player] += circle.remove().toLong()
            } else {
                circle.move(1)
                circle.add(round)
            }
            player = (player + 1) % players
        }
        return scores.max()
    }
}