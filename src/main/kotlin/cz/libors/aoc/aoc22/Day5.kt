package cz.libors.aoc.aoc22

import cz.libors.util.findInts
import cz.libors.util.readToLines
import java.util.*
import kotlin.collections.HashMap

object Day5 {

    private fun perform(data: List<String>, keepOrder: Boolean): Stacks {
        val stacks = Stacks(keepOrder)
        var moves = true
        for (line in data) {
            if (moves) {
                for (i in 1..line.length step 4)
                    if (line[i] in 'A'..'Z') stacks.add(line[i], (i - 1) / 4 + 1)
                if (line.isEmpty()) moves = false
            } else {
                line.findInts().let { p -> stacks.move(Move(p[1], p[2], p[0])) }
            }
        }
        return stacks
    }

    private fun task1(input: List<String>) = perform(input, false)
    private fun task2(input: List<String>) = perform(input, true)

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input5.txt")
        println(task1(input).top())
        println(task2(input).top())
    }

    data class Move(val from: Int, val to: Int, val howMany: Int)

    private class Stacks(val keepOrder: Boolean) {
        private val stacks: MutableMap<Int, Deque<Char>> = HashMap()

        fun move(move: Move) {
            val bricks = mutableListOf<Char>()
            repeat(move.howMany) { bricks.add(stacks[move.from]?.removeLast()!!) }
            if (keepOrder) bricks.reverse()
            stacks[move.to]?.addAll(bricks)
        }

        fun add(char: Char, where: Int) {
            stacks.getOrPut(where) { LinkedList() }.addFirst(char)
        }

        fun top(): String =
            stacks
                .filter { !it.value.isEmpty() }
                .map { s -> Pair(s.key, s.value.last) }
                .sortedBy { it.first }
                .map { it.second }
                .joinToString("")
    }
}