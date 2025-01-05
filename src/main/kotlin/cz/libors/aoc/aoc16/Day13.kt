package cz.libors.aoc.aoc16

import cz.libors.util.*

@Day("A Maze of Twisty Little Cubicles")
object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input13.txt").toInt()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Int) = bfs(Point(1, 1), endFn = { it == Point(31, 39) },
        neighboursFn = { it.neighbours().filter { n -> n.x >= 0 && n.y >= 0 && isOk(n, input) } }).getScore()!!

    private fun task2(input: Int) = bfsToAll(Point(1, 1),
        neighboursFn = {
            it.neighbours().filter { n -> n.x in (0..51) && n.y in (0..51) && isOk(n, input) }
        }).distances().count { it.value <= 50 }

    private fun isOk(p: Point, input: Int) = with(p) {
        Integer.bitCount(x * x + 3 * x + 2 * x * y + y + y * y + input) % 2 == 0
    }
}