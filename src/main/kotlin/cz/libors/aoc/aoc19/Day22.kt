package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.readToLines
import java.lang.RuntimeException

@Day("Slam Shuffle")
object Day22 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input22.txt")
        println(task1(input))
        TODO("task 2")
    }

    private fun task1(input: List<String>): Long =
        runSteps(getSteps(input), OneCard(10007, 2019)).position

    private fun task2(input: List<String>) {
        var position = 2020L
        var repetition = 0L
        val steps = getSteps(input)
        do {
            position = runSteps(steps, OneCard(119315717514047L, position)).position
            repetition++
            if (repetition % 1000000 == 0L ) println(repetition)
        } while ((position != 2020L))
        println(repetition)
        val modRepetition = 101741582076661L % repetition
        println(modRepetition)

    }

    private fun runSteps(steps: List<(OneCard) -> OneCard>, initCard: OneCard): OneCard {
        var c = initCard
        for (step in steps) c = step.invoke(c)
        return c
    }


    private fun getSteps(steps: List<String>) = steps.map { getStep(it) }

    private fun getStep(description: String): (OneCard) -> OneCard =
        if (description == "deal into new stack") {
            val f: (OneCard) -> OneCard = { it.deal() }
            f
        } else if (description.startsWith("deal with increment")) {
            val n = description.substringAfterLast(" ").toLong()
            val f: (OneCard) -> OneCard = { it.dealIncrement(n) }
            f
        } else if (description.startsWith("cut")) {
            val n = description.substringAfterLast(" ").toLong()
            val f: (OneCard) -> OneCard = { it.cut(n) }
            f
        } else {
            throw RuntimeException("Unknown description : $description")
        }

    private class OneCard(private val total: Long, val position: Long) {

        fun deal() = OneCard(total, total - position - 1)

        fun cut(n: Long): OneCard = if (n < 0) cut(total + n) else {
            val pos = if (n <= position) position - n else total - (n - position)
            OneCard(total, pos)
        }

        fun dealIncrement(n: Long) = OneCard(total, (n * position) % total)
    }
}