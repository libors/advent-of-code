package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.findLongs
import cz.libors.util.readToText
import java.lang.RuntimeException

@Day("1202 Program Alarm")
object Day2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input2.txt").findLongs()
        println(task1(input))
        println(task2(input))
    }

    private fun initCode(input: List<Long>, first: Long, second: Long): LongArray {
        val clone = input.toLongArray()
        clone[1] = first
        clone[2] = second
        return clone
    }

    private fun task1(input: List<Long>) = Computer.create(initCode(input, 12, 2))
        .runCode()
        .getMemoryState()[0]

    private fun task2(input: List<Long>): Long {
        for (noun in 0..99) {
            for (verb in 0..99) {
                val i = initCode(input, noun.toLong(), verb.toLong())
                if (Computer.create(i).runCode().getMemoryState()[0] == 19690720L) {
                    return 100L * noun + verb
                }
            }
        }
        throw RuntimeException("No combination found")
    }
}