package cz.libors.aoc.aoc23

import cz.libors.util.*
import cz.libors.util.Vector

@Day(name = "Clumsy Crucible")
object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input17.txt").toPointsWithValue()
            .associate { it.first to it.second.digitToInt() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Map<Point, Int>): Int {
        val box = input.keys.boundingBox()
        return dijkstra(
            Position(box.first, Vector.RIGHT, 0),
            endFn = { it.p == box.second },
            distanceFn = { _, b -> input[b.p]!! },
            neighboursFn = { neighbours(it).filter { x -> box.contains(x.p) } }).getScore()!!
    }

    private fun task2(input: Map<Point, Int>): Int {
        val box = input.keys.boundingBox()
        return dijkstra(
            Position(box.first, Vector.RIGHT, 0),
            endFn = { it.p == box.second && it.dirMoves >= 4 },
            distanceFn = { _, b -> input[b.p]!! },
            neighboursFn = { neighbours2(it).filter { x -> box.contains(x.p) } }).getScore()!!
    }

    private fun neighbours(pos: Position): Iterable<Position> {
        val moves = mutableListOf<Position>()
        if (pos.dirMoves < 3) {
            moves.add(Position(pos.p + pos.dir, pos.dir, pos.dirMoves + 1))
        }
        val right = pos.dir.turnRight()
        moves.add(Position(pos.p + right, right, 1))
        val left = pos.dir.turnLeft()
        moves.add(Position(pos.p + left, left, 1))
        return moves
    }

    private fun neighbours2(pos: Position): Iterable<Position> {
        val moves = mutableListOf<Position>()
        if (pos.dirMoves < 10) {
            moves.add(Position(pos.p + pos.dir, pos.dir, pos.dirMoves + 1))
        }
        if ((pos.dirMoves >= 4 || pos.dirMoves == 0)) {
            val right = pos.dir.turnRight()
            moves.add(Position(pos.p + right, right, 1))
            val left = pos.dir.turnLeft()
            moves.add(Position(pos.p + left, left, 1))
        }
        return moves
    }

    private data class Position(val p: Point, val dir: Vector, val dirMoves: Int)
}