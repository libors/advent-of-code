package cz.libors.aoc.aoc22

import cz.libors.util.*

@Day(name = "Unstable Diffusion")
object Day23 {

    private val rules = listOf(
        Decision(listOf(Vector.UP, Vector.RIGHT_UP, Vector.LEFT_UP), Vector.UP),
        Decision(listOf(Vector.DOWN, Vector.RIGHT_DOWN, Vector.LEFT_DOWN), Vector.DOWN),
        Decision(listOf(Vector.LEFT, Vector.LEFT_UP, Vector.LEFT_DOWN), Vector.LEFT),
        Decision(listOf(Vector.RIGHT, Vector.RIGHT_UP, Vector.RIGHT_DOWN), Vector.RIGHT)
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val elves = readToLines("input23.txt")
            .toPointsWithValue()
            .filter { it.second == '#' }
            .map { it.first }
            .toSet()

        println(task1(elves))
        println(task2(elves))
    }

    private fun task1(elves: Set<Point>): Int {
        var initRule = 0
        var roundElves = elves
        for (round in 1..10)
            roundElves = nextStep(roundElves, initRule++)
        return roundElves.boundingBox()
            .let { (it.second.x - it.first.x + 1) * (it.second.y - it.first.y + 1) } - roundElves.size
    }

    private fun task2(elves: Set<Point>): Int {
        var initRule = 0
        var roundElves = elves
        while (true) {
            val nextRound = nextStep(roundElves, initRule++)
            if (nextRound == roundElves) return initRule else roundElves = nextRound
        }
    }

    private fun nextStep(roundElves: Set<Point>, initRule: Int): Set<Point> {
        var roundElves1 = roundElves
        val nextMoves = roundElves1.associateWith { pickNewPos(it, roundElves1, initRule) }
        val nextMovePoints = nextMoves.values.groupingBy { it }
            .eachCount()
            .filter { it.value == 1 }
            .map { it.key }
            .toSet()
        roundElves1 = nextMoves.map { e -> if (nextMovePoints.contains(e.value)) e.value else e.key }.toSet()
        return roundElves1
    }

    private fun pickNewPos(current: Point, elves: Set<Point>, initRule: Int): Point {
        if (current.neighbours().none { elves.contains(it) }
            && current.diagonalPoints().none { elves.contains(it) }) return current
        for (rule in initRule..initRule + 3) {
            val next = rules[rule % 4].tryIt(current, elves)
            if (next != current) return next
        }
        return current
    }

    private data class Decision(val ifFree: List<Vector>, val thenSuggest: Vector) {
        fun tryIt(current: Point, elves: Set<Point>): Point = if (ifFree.all { !elves.contains(current + it) })
            current + thenSuggest else current
    }
}