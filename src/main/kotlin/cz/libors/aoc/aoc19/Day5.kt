package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.findLongs
import cz.libors.util.readToText

@Day("Sunny with a Chance of Asteroids")
object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input5.txt").findLongs().toLongArray()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(code: LongArray) = Computer.create(code, Input.value(1))
        .runCode().getLastOutput()

    private fun task2(code: LongArray) = Computer.create(code, Input.value(5))
        .runCode().getLastOutput()
}