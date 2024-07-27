package cz.libors.aoc.aoc22

import cz.libors.util.*

@Day(name = "Supply Stacks")
object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input5.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String) = perform(input, false)
    private fun task2(input: String) = perform(input, true)

    private fun perform(data: String, keepOrder: Boolean): String {
        val (stackString, moveString) = data.splitByEmptyLine()
        val moves = moveString.lines().map { it.findInts() }.map { Move(it[1], it[2], it[0]) }
        val stacks = multiMap<Int, Char>()
        for (line in stackString.lines()) {
            line.take(1, 4).forEachIndexed { index, c ->
                if (c.isLetter()) stacks.add(index, c)
            }
        }
        for (move in moves) {
            val from = stacks[move.from - 1]
            val to = stacks[move.to - 1]
            var x = mutableListOf<Char>()
            for (i in 1..move.howMany)
                x.add(from!!.removeFirst())
            if (keepOrder)
                x = x.reversed().toMutableList()
            for (ch in x)
                to!!.addFirst(ch)
        }
        return stacks.toSortedMap().values.map { it.first }.joinToString("")
    }

    data class Move(val from: Int, val to: Int, val howMany: Int)
}