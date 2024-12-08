package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("Ceres Search")
object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input4.txt").toPointsWithValue().toMap()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Map<Point, Char>): Int {
        val box = input.keys.boundingBox()
        var result = 0
        val allDirections = Vector.orthogonalVectors() + Vector.diagonalVectors()
        for (x in box.first.x..box.second.x) {
            for (y in box.first.y..box.second.y) {
                result += allDirections.map { isXmas(input, Point(x, y), it) }.count { it }
            }
        }
        return result
    }

    private fun task2(input: Map<Point, Char>): Int {
        val box = input.keys.boundingBox()
        var result = 0
        for (x in box.first.x + 1 until box.second.x) {
            for (y in box.first.y + 1 until box.second.y) {
                if (isCross(input, Point(x, y))) result++
            }
        }
        return result
    }

    private fun isCross(input: Map<Point, Char>, p: Point): Boolean {
        return input[p] == 'A' &&
                (input[p + Vector.LEFT_UP] == 'M' && input[p + Vector.RIGHT_DOWN] == 'S' ||
                        input[p + Vector.LEFT_UP] == 'S' && input[p + Vector.RIGHT_DOWN] == 'M') &&
                (input[p + Vector.RIGHT_UP] == 'M' && input[p + Vector.LEFT_DOWN] == 'S' ||
                        input[p + Vector.RIGHT_UP] == 'S' && input[p + Vector.LEFT_DOWN] == 'M')
    }

    private fun isXmas(points: Map<Point, Char>, start: Point, vector: Vector) = "XMAS" == (0..3)
        .map { points[start + vector * it] }
        .joinToString("")

}