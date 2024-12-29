package cz.libors.aoc.aoc18

import cz.libors.util.Day
import cz.libors.util.Point
import cz.libors.util.readToText

@Day("Chronal Charge")
object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input11.txt").toInt()

        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Int): String {
        val grid = generateGrid(input)
        val prevGrid = generateGrid(input)
        findLargestRegion(grid, 2, prevGrid)
        val coord = findLargestRegion(grid, 3, prevGrid).coords
        return "${coord.x},${coord.y}"
    }

    private fun task2(input: Int): String {
        val grid = generateGrid(input)
        val prevGrid = generateGrid(input)
        val maxResult = (2..300).map { it to findLargestRegion(grid, it, prevGrid) }.maxBy { it.second.max }
        return "${maxResult.second.coords.x},${maxResult.second.coords.y},${maxResult.first}"
    }

    private fun findLargestRegion(grid: Array<IntArray>, size: Int, prevSizeGrid: Array<IntArray>): FindResult {
        var max = Int.MIN_VALUE
        var coord = Point(-1, -1)
        for (x in 0..300 - size) {
            for (y in 0..300 - size) {
                val sum = sumPower(x, y, grid, size, prevSizeGrid[x][y])
                prevSizeGrid[x][y] = sum
                if (sum > max) {
                    max = sum
                    coord = Point(x + 1, y + 1)
                }
            }
        }
        return FindResult(max, coord)
    }

    private fun generateGrid(input: Int): Array<IntArray> {
        val grid = Array(300) { IntArray(300) }
        for (x in 0 until 300)
            for (y in 0 until 300)
                grid[x][y] = powLevel(x + 1, y + 1, input)
        return grid
    }

    private fun sumPower(x: Int, y: Int, grid: Array<IntArray>, size: Int, prevSum: Int): Int {
        var sum = prevSum
        for (i in x until x + size) sum += grid[i][y + size - 1]
        for (i in y until y + size - 1) sum += grid[x + size - 1][i]
        return sum
    }

    private fun powLevel(x: Int, y: Int, input: Int): Int {
        val rackId = x + 10
        var res = rackId * y
        res += input
        res *= rackId
        res = digit3(res)
        res -= 5
        return res
    }

    private fun digit3(x: Int) = (x % 1000) / 100

    private data class FindResult(val max: Int, val coords: Point)
}