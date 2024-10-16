package cz.libors.aoc.aoc21

import cz.libors.util.*

@Day("Giant Squid")
object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input4.txt").splitByEmptyLine()
        val nums = input[0].split(",").map { it.toInt() }
        val boards = input.drop(1).map { it.splitByNewLine().map { x -> x.findInts() } }.map { Board(it) }
        println(task1(nums, boards))
        boards.forEach { it.clear() }
        println(task2(nums, boards))
    }

    private fun task1(nums: List<Int>, boards: List<Board>): Int {
        val drawn = mutableSetOf<Int>()
        for (i in 0..nums.size) {
            val n = nums[i]
            drawn.add(n)
            for (b in boards) {
                val won = b.drawNumber(n)
                if (won) {
                    return b.notTaken(drawn).sum() * n
                }
            }
        }
        throw IllegalStateException("No drawn")
    }

    private fun task2(nums: List<Int>, boards: List<Board>): Int {
        val alreadyWon = mutableSetOf<Board>()
        val drawn = mutableSetOf<Int>()
        for (i in 0..nums.size) {
            val n = nums[i]
            drawn.add(n)
            for (b in boards) {
                if (b !in alreadyWon) {
                    val won = b.drawNumber(n)
                    if (won) {
                        alreadyWon.add(b)
                        if (alreadyWon.size == boards.size) {
                            return b.notTaken(drawn).sum() * n
                        }
                    }
                }
            }
        }
        throw IllegalStateException("No drawn")
    }


    private class Board(private val numbers: List<List<Int>>){
        val rows = mutableMapOf<Int, Int>()
        val cols = mutableMapOf<Int, Int>()

        fun clear() {
            rows.clear()
            cols.clear()
        }

        fun drawNumber(n: Int): Boolean {
            for (i in 0 until numbers.size)
                for (j in 0 until numbers[i].size)
                    if (numbers[i][j] == n) {
                        rows.merge(i, 1, Int::plus)
                        cols.merge(j, 1, Int::plus)
                    }
            return rows.values.contains(5) || cols.values.contains(5)
        }

        fun notTaken(nums: Set<Int>): List<Int> {
            return numbers.flatten().filter { !nums.contains(it) }
        }
    }

}