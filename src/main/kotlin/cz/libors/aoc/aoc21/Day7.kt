package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.findLongs
import cz.libors.util.readToText
import kotlin.math.abs

@Day("The Treachery of Whales")
object Day7 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input7.txt").findLongs()
        println(task1(input))
        println(task2(input))
    }

    fun task1(input: List<Long>) = (input.minOf { it }..input.maxOf { it })
        .map { align -> input.sumOf { abs(align - it) } }.minOf { it }

    fun task2(input: List<Long>) = (input.minOf { it }..input.maxOf { it })
        .map { align -> input.sumOf { abs(align - it) * (abs(align - it) + 1) } / 2 }.minOf { it }
}