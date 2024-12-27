package cz.libors.aoc.aoc20

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Handheld Halting")
object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input8.txt").map { Pair(it.substring(0, 3), it.substring(4).toInt()) }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Pair<String, Int>>) = run(input)!!.acc

    private fun task2(input: List<Pair<String, Int>>): Int {
        val prog = input.toMutableList()
        for (i in input.indices) {
            val inst = prog[i]
            when(inst.first) {
                "nop" -> {
                    prog[i] = Pair("jmp", inst.second)
                    val result = run(prog)
                    if (result != null && !result.cycle) return result.acc else prog[i] = Pair("nop", inst.second)
                }
                "jmp" -> {
                    prog[i] = Pair("nop", inst.second)
                    val result = run(prog)
                    if (result != null && !result.cycle) return result.acc else prog[i] = Pair("jmp", inst.second)
                }
                else -> continue
            }
        }
        throw IllegalArgumentException()
    }

    private fun run(input: List<Pair<String, Int>>): Result? {
        var acc = 0
        var pointer = 0
        val visited = BooleanArray(input.size)
        while(true) {
            if (pointer < 0 || pointer > input.size) return null
            if (pointer == input.size) return Result(false, acc)
            if (visited[pointer]) return Result(true, acc)
            visited[pointer] = true
            val inst = input[pointer]
            when(inst.first) {
                "acc" -> { acc += inst.second; pointer++ }
                "nop" -> pointer++
                "jmp" -> pointer += inst.second
            }
        }
    }

    private data class Result(val cycle: Boolean, val acc: Int)
}