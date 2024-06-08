package cz.libors.aoc.aoc23

import cz.libors.util.*
import cz.libors.util.Vector
import java.util.*

@Day(name = "Clumsy Crucible")
object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input17.txt").toPointsWithValue()
            .associate { it.first to it.second.digitToInt() }
        println(task1(input))
        //println(task2(input))
        TODO("bug in the code, not working for part 2")
    }

    private fun task1(input: Map<Point, Int>) = Crucible(input, false).find()
    private fun task2(input: Map<Point, Int>) = Crucible(input, true).find()

    private class Crucible(val maze: Map<Point, Int>, val ultra: Boolean) {
        private val maxDirMoves = if (ultra) 10 else 3
        private val visited = mutableMapOf<Visit, Int>()
        private val queue = PriorityQueue<State>(Comparator.comparing { it.heat })
        private val box = maze.keys.boundingBox()
        private val start = box.first
        private val target = box.second
        private var best = Integer.MAX_VALUE

        fun find(): Int {
            queue.add(State(Visit(start, Vector.RIGHT, 0), 0))
            while (queue.isNotEmpty()) {
                process(queue.remove())
            }
            return best
        }

        private fun process(state: State) {
            val pos = state.pos
            if (pos.p == target) {
                if (state.heat < best && (!ultra || pos.dirMoves >= 4)) {
                    best = state.heat
                    println(best)
                    return
                }

            }
            val fromCache = visited[pos]
            if (fromCache != null && fromCache <= state.heat) {
                return
            } else {
                visited[pos] = state.heat
            }
            val moves = mutableListOf<Visit>()
            if (pos.dirMoves < maxDirMoves) {
                moves.add(Visit(pos.p.plus(pos.dir), pos.dir, pos.dirMoves + 1))
            }
            if (!ultra || (pos.dirMoves >= 4 || state.heat == 0 /* first move */)) {
                val right = pos.dir.turnRight()
                moves.add(Visit(pos.p.plus(right), right, 1))
                val left = pos.dir.turnLeft()
                moves.add(Visit(pos.p.plus(left), left, 1))
            }
            moves.filter { box.contains(it.p) }.forEach {
                queue.add(State(it, state.heat + maze[it.p]!!))
            }
        }
    }

    private data class Visit(val p: Point, val dir: Vector, val dirMoves: Int)
    private data class State(val pos: Visit, val heat: Int)
}