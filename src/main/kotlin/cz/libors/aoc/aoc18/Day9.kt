package cz.libors.aoc.aoc18

import cz.libors.util.*

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
        var player = 0
        val scores = LongArray(players)
        var x = CNode(0)
        for (round in 1..maxRound) {
            if (round % 23 == 0) {
                scores[player] += round.toLong()
                x = x.move(-7)
                scores[player] += x.value.toLong()
                x = x.removeGetRight()
            } else {
                x = x.right.addRight(round)
            }
            player = (player + 1) % players
        }
        return scores.max()
    }
}