package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("No Such Thing as Too Much")
object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input17.txt").map { it.toInt() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(containers: List<Int>) = possibilities(containers, 150).sum()
    private fun task2(containers: List<Int>): Int = possibilities(containers, 150)
        .mapIndexed { contNum, possible -> contNum to possible  }
        .filter { it.second != 0 }
        .minBy { it.first}.second

    private fun possibilities(containers: List<Int>, amount: Int): IntArray {
        val contNum = IntArray(containers.size)

        fun dfs(remain: Int, idx: Int, cNum: Int) {
            if (remain == 0) contNum[cNum]++
            if (remain < 0 || idx == containers.size) return
            for (i in idx  until containers.size) {
                dfs(remain - containers[i], i + 1, cNum + 1)
            }
        }

        dfs(amount, 0, 0)
        return contNum
    }
}