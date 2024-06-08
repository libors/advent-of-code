package cz.libors.aoc.aoc23

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines

@Day("Wait For It")
object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = readToLines("input6.txt").map { it.findInts() }
        val input = lines[0].zip(lines[1]).map { BoatRun(it.first, it.second.toLong()) }
        println(task1(input))
        println(task2(input))
    }

    private fun task2(boatRuns: List<BoatRun>): Long {
        val actualBoatRun = BoatRun(
            boatRuns.joinToString("") { it.time.toString() }.toInt(),
            boatRuns.joinToString("") { it.distance.toString() }.toLong())
        return task1(listOf(actualBoatRun))
    }

    private fun task1(boatRuns: List<BoatRun>) = boatRuns.map { run ->
        (1 until run.time)
            .map { hold -> distance(hold, run.time) }
            .count { it > run.distance }
            .toLong()
    }.reduce { a, b -> a * b }

    private fun distance(holdTime: Int, totalTime: Int) = (totalTime - holdTime) * holdTime.toLong()

    private data class BoatRun(val time: Int, val distance: Long)
}