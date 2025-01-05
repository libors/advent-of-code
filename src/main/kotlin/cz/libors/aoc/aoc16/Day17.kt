package cz.libors.aoc.aoc16

import cz.libors.util.*

@Day("Two Steps Forward")
object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input17.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String) = bfs(Pair(input, Point(0, 0)), { it.second == Point(3, 3) }, ::neighbours)
        .getPath().last().first.substringAfter(input)

    private fun task2(input: String): Int {
        var maxLength = 0
        val target = Point(3, 3)

        fun dfs(pos: Pair<String, Point>, length: Int) {
            if (pos.second == target) {
                if (maxLength < length) maxLength = length
                return
            }
            for (n in neighbours(pos)) {
                dfs(n, length + 1)
            }
        }

        dfs(Pair(input, Point(0, 0)), 0)
        return maxLength
    }

    private fun neighbours(pair: Pair<String, Point>): Iterable<Pair<String, Point>> {
        val hash = md5(pair.first)
        val open = "bcdef"
        val result = mutableListOf<Pair<String, Point>>()
        if (pair.second.y > 0 && open.contains(hash[0])) result.add(Pair(pair.first + 'U', pair.second.up()))
        if (pair.second.y < 3 && open.contains(hash[1])) result.add(Pair(pair.first + 'D', pair.second.down()))
        if (pair.second.x > 0 && open.contains(hash[2])) result.add(Pair(pair.first + 'L', pair.second.left()))
        if (pair.second.x < 3 && open.contains(hash[3])) result.add(Pair(pair.first + 'R', pair.second.right()))
        return result
    }
}