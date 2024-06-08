package cz.libors.aoc.aoc22

import cz.libors.util.Body
import cz.libors.util.Day
import cz.libors.util.Point
import cz.libors.util.readToText

private typealias Rock = Body

@Day(name = "Pyroclastic Flow")
object Day17 {

    private val brickArray = listOf(
        Rock(setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0))),
        Rock(setOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 2))),
        Rock(setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, 1), Point(2, 2))),
        Rock(setOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3))),
        Rock(setOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1)))
    )

    private fun doIt(
        initGround: Rock,
        dirArray: CharArray,
        iterations: Long,
        dirStart: Int = 0,
        brickStart: Int = 0
    ): Long {
        var brickIdx = brickStart
        var dirIdx = dirStart
        val periodHash = mutableMapOf<Snapshot, Pair<Int, Long>>()
        var ground = initGround

        for (brickNo in 0 until iterations) {
            val snapshot = Snapshot(ground.surface(), dirIdx % dirArray.size, brickIdx % brickArray.size)
            if (periodHash.contains(snapshot)) {
                val (ppHeight, ppBricks) = periodHash[snapshot]!!
                val curHeight = ground.height()
                val pHeight = curHeight - ppHeight
                val pBricks = brickNo - ppBricks
                val periods = (iterations - ppBricks) / pBricks
                val pTotalBricks = periods * pBricks
                val pTotalHeight = periods * pHeight
                val remainingBricks = iterations - pTotalBricks - ppBricks
                val remainingHeight =
                    doIt(ground, dirArray, remainingBricks, dirStart = dirIdx, brickStart = brickIdx) - curHeight
                return remainingHeight + pTotalHeight + ppHeight
            } else {
                periodHash[snapshot] = Pair(ground.height(), brickNo)
            }

            var brick = brickArray[brickIdx++ % brickArray.size]
                .right(2)
                .down(ground.height() + 3) // reverse plane, actually goes up
            var collision = false
            do {
                val left = dirArray[dirIdx++ % dirArray.size] == '<'
                if (left) {
                    if (brick.leftest() > 0 && !brick.left().collides(ground))
                        brick = brick.left()
                } else {
                    if (brick.rightest() < 6 && !brick.right().collides(ground))
                        brick = brick.right()
                }
                if (brick.lowest() == 0 || brick.progress().collides(ground)) {
                    ground += brick
                    collision = true
                } else {
                    brick = brick.progress()
                }
            } while (!collision)
        }
        return ground.height().toLong()
    }

    private fun task1(input: CharArray) = doIt(Rock(setOf()), input, 2022)
    private fun task2(input: CharArray) = doIt(Rock(setOf()), input, 1000000000000)

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input17.txt").toCharArray()
        println(task1(input))
        println(task2(input))
    }

    private data class Snapshot(val surface: List<Int>, val dirIdx: Int, val brickIdx: Int)

    private fun Rock.surface(): List<Int> {
        if (points.isEmpty()) return listOf(0, 0, 0, 0, 0, 0, 0)
        val x = points.groupBy { it.x }.mapValues { e -> e.value.maxOf { p -> p.y } }.toSortedMap()
        val min = x.minOf { it.value }
        return x.mapValues { e -> e.value - min }.values.toList()
    }

    private fun Rock.height() = if (points.isEmpty()) 0 else highest() + 1
    private fun Rock.progress() = up()
}