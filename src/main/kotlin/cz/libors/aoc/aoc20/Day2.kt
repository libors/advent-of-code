package cz.libors.aoc.aoc20

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Password Philosophy")
object Day2 {

    private val regex = Regex("^(\\d+)-(\\d+) (.): (.*)$")  // 1-3 a: abcde

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input2.txt")
            .map { regex.matchEntire(it)!!.groupValues.let { x -> Record(x[1].toInt(), x[2].toInt(), x[3][0], x[4]) } }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Record>) = input.count { r -> r.pass.count { c -> c == r.char } in r.a..r.b }
    private fun task2(input: List<Record>) = input.count { r ->
        val first = r.pass[r.a - 1] == r.char
        val second = r.pass[r.b - 1] == r.char
        first && !second || !first && second
    }

    private data class Record(val a: Int, val b: Int, val char: Char, val pass: String)
}