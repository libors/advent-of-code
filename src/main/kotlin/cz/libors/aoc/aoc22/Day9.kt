package cz.libors.aoc.aoc22

import cz.libors.util.readToLines
import kotlin.math.abs

object Day9 {

    private fun solve(moves: List<Move>, ropeLength: Int): Int {
        val visited = mutableSetOf<Pair<Int, Int>>()
        val rope = Array(ropeLength) { Pair(0, 0) }
        visited.add(rope[ropeLength - 1])
        for (move in moves) {
            for (i in 0 until move.howMany) {
                rope[0] = Pair(rope[0].first + move.xChange, rope[0].second + move.yChange)
                for (node in 1 until ropeLength)
                    rope[node] = tailMove(rope[node - 1], rope[node])
                visited.add(rope[ropeLength - 1])
            }
        }
        return visited.size
    }

    private fun task1(moves: List<Move>) = solve(moves, 2)
    private fun task2(moves: List<Move>) = solve(moves, 10)

    private fun tailMove(head: Pair<Int, Int>, tail: Pair<Int, Int>): Pair<Int, Int> {
        if (head.adjacent(tail)) return tail
        val xMove = if (head.first > tail.first) 1 else if (head.first < tail.first) -1 else 0
        val yMove = if (head.second > tail.second) 1 else if (head.second < tail.second) -1 else 0
        return Pair(tail.first + xMove, tail.second + yMove)
    }

    private fun Pair<Int, Int>.adjacent(other: Pair<Int, Int>) =
        abs(this.first - other.first) <= 1 && abs(this.second - other.second) <= 1

    private fun toMove(it: String): Move {
        val parts = it.split(' ')
        val num = parts[1].toInt()
        return when (parts[0]) {
            "U" -> Move(0, -1, num)
            "D" -> Move(0, 1, num)
            "R" -> Move(1, 0, num)
            "L" -> Move(-1, 0, num)
            else -> throw RuntimeException()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input9.txt").map { toMove(it) }
        println(task1(input))
        println(task2(input))
    }

    data class Move(val xChange: Int, val yChange: Int, val howMany: Int)
}