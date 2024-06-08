package cz.libors.aoc.aoc22

import cz.libors.util.Day
import cz.libors.util.readToLines

private typealias Tree = Pair<Int, Int>

@Day(name = "Treetop Tree House")
object Day8 {

    private fun visibleFromGround(sizes: List<Int>): List<Int> {
        var max = -1
        val result = mutableListOf<Int>()
        for (i in sizes.indices) {
            if (sizes[i] > max) {
                result.add(i)
                max = sizes[i]
            }
        }
        return result
    }

    private fun visibleFromHouse(sizes: List<Int>, houseSize: Int): Int {
        for (i in sizes.indices) {
            if (sizes[i] >= houseSize) {
                return i + 1
            }
        }
        return sizes.size
    }

    private fun task1(input: List<List<Int>>): Int {
        val visible = mutableSetOf<Tree>()
        val columns = input[0].size
        for (rowIdx in input.indices) {
            val visibleFromLeft = visibleFromGround(input[rowIdx])
            for (i in visibleFromLeft)
                visible.add(Tree(rowIdx, i))
            val visibleFromRight = visibleFromGround(input[rowIdx].reversed())
            for (i in visibleFromRight)
                visible.add(Tree(rowIdx, columns - 1 - i))
        }
        for (colIdx in 0 until columns) {
            val treeLine = input.map { it[colIdx] }
            val visibleFromTop = visibleFromGround(treeLine)
            for (i in visibleFromTop)
                visible.add(Tree(i, colIdx))
            val visibleFromBottom = visibleFromGround(treeLine.reversed())
            for (i in visibleFromBottom)
                visible.add(Tree(input.size - 1 - i, colIdx))
        }

        return visible.size
    }

    private fun task2(input: List<List<Int>>): Int {
        val columns = input[0].size
        var maxScore = 0
        for (rowIdx in 1 until input.size - 1) {
            for (colIdx in 1 until columns - 1) {
                val house = input[rowIdx][colIdx]
                val toRight = visibleFromHouse(input[rowIdx].subList(colIdx + 1, columns), house)
                val toLeft = visibleFromHouse(input[rowIdx].subList(0, colIdx).reversed(), house)
                val toBottom = visibleFromHouse(input.map { it[colIdx] }.subList(rowIdx + 1, input.size), house)
                val toTop = visibleFromHouse(input.map { it[colIdx] }.subList(0, rowIdx).reversed(), house)
                val score = toLeft * toRight * toTop * toBottom

                if (score > maxScore) maxScore = score
            }
        }
        return maxScore
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input8.txt").map { it.toCharArray().map { ch -> ch.digitToInt() } }
        println(task1(input))
        println(task2(input))
    }
}