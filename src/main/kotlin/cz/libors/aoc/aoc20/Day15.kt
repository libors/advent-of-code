package cz.libors.aoc.aoc20

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToText

@Day("Rambunctious Recitation")
object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input15.txt").findInts()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Int>) = play(input, 2020)
    private fun task2(input: List<Int>) = play(input, 30000000)

    private fun play(input: List<Int>, untilRound: Int): Int {
        val map = mutableMapOf<Int, Int>()
        input.forEachIndexed { idx, num -> map[num] = idx + 1 }
        var lastSeen = 0
        var spoken = input[input.size - 1]
        for (i in input.size + 1 .. untilRound) {
            spoken = if (lastSeen > 0) i - 1 - lastSeen else 0
            lastSeen = map[spoken]?:0
            map[spoken] = i
        }
        return spoken
    }
}