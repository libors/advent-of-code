package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines
import kotlin.math.min

@Day("Reindeer Olympics")
object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input14.txt").map {
            val nums = it.findInts()
            Reindeer(it.substringBefore(' '), nums[0], nums[1], nums[2])
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(reindeers: List<Reindeer>) = reindeers.maxOf { it.travelInSecs(2503) }

    private fun task2(reindeers: List<Reindeer>): Int {
        val scores = IntArray(reindeers.size)
        for (i in 1..2503) {
            val dists = reindeers.map { it.travelInSecs(i) }
            val max = dists.max()
            dists.forEachIndexed { idx, dist -> if (dist == max) scores[idx]++ }
        }
        return scores.max()
    }

    private data class Reindeer(val name: String, val speed: Int, val time: Int, val rest: Int) {
        fun travelInSecs(num: Int): Int {
            return (num / (time + rest)) * speed * time + min(time, (num % (time + rest))) * speed
        }
    }
}