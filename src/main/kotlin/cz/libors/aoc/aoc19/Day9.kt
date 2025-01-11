package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.findLongs
import cz.libors.util.readToText

@Day("Sensor Boost")
object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input9.txt").findLongs().toLongArray()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(code: LongArray): Long = Computer.create(code, Input.value(1)).runCode().getLastOutput()
    private fun task2(code: LongArray): Long = Computer.create(code, Input.value(2)).runCode().getLastOutput()
}