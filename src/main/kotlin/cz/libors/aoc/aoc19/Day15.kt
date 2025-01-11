package cz.libors.aoc.aoc19

import cz.libors.util.*
import cz.libors.util.Vector
import java.lang.RuntimeException
import java.util.*

@Day("Oxygen System")
object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input15.txt").findLongs().toLongArray()
        TODO("not working")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(code: LongArray): Int {
        val plane = RepairDroid(code.copyOf()).runCode().getPlane()
        val oxygenPos = plane.filterValues { it == 2L }.toList()[0].first
        return findShortestPath(plane, Point(0, 0), oxygenPos)
    }

    private fun task2(code: LongArray): Int {
        val plane = RepairDroid(code.copyOf()).runCode().getPlane()
        var onPath = plane
            .filterValues { onPath(it) }
            .toMutableMap()
        var i = 0
        while (onPath.isNotEmpty()) {
            i++
            val oxygens = onPath.filterValues { it == 2L }
            oxygens.forEach { (k, _) -> onPath.remove(k) }
            val adjacentToOxygens = oxygens.keys.map { it.neighbours() }.flatten().toSet()
            onPath = onPath.mapValues {
                if (adjacentToOxygens.contains(it.key)) 2L else 1L
            }.toMutableMap()
        }
        return i - 1
    }

    private fun onPath(i: Long) = i == 1L || i == 2L

    private fun findShortestPath(plane: Map<Point, Long>, from: Point, to: Point): Int {
        println(plane)
        return 1
//        val graph = GraphBuilder(::onPath).build(plane)
//        val shortestPath = DijkstraShortestPath.findPathBetween(graph, from, to)
//        return shortestPath.edgeList.size
    }

    private class RepairDroid(codeInput: LongArray) :
        ComputerImpl("repair droid", codeInput, Input.value(), ArrayOutput()) {
        private val plane = mutableMapOf<Point, Long>()
        private val printer = PlanePrinter(mapOf(0L to '#', 1L to ' ', 2L to '!', -1L to '.'), -1L)
        private var currentPos = Point(0, 0)
        private var lastInput = 0L

        private val directions = listOf(Vector.UP, Vector.DOWN, Vector.LEFT, Vector.RIGHT)
        private var returning = false
        private val state = LinkedList<VisitPoint>()

        init {
            plane[currentPos] = 1L
            state.add(VisitPoint(currentPos))
        }

        private fun dir(code: Long) = directions[code.toInt() - 1]

        override fun runCode() = super.runCode() as RepairDroid

        override fun writeOutput(value: Long) {
            when (value) {
                0L -> {
                    plane[currentPos + dir(lastInput)] = 0L
                }
                1L, 2L -> {
                    currentPos += dir(lastInput)
                    if (returning) {
                        returning = false
                    } else {
                        state.last.lastSuccess = state.last.currentDir
                        state.add(VisitPoint(currentPos))
                    }
                    plane[currentPos] = value
                }
                else -> throw RuntimeException("Unknown output value: $value")
            }
        }

        override fun readInput() = computerInput()

        private fun computerInput(): Long {
            val point = state.last!!
            require(point.pos == currentPos) { "${point.pos} != $currentPos" }
            while (point.currentDir < 5 && plane[point.pos + dir(point.currentDir)] != null) point.currentDir++
            lastInput = if (point.currentDir == 5L) {
                if (state.size == 1) {
                    updatePos(-1) // exit
                    cdebug(printer.print(plane, Point(0, 0)))
                    1 // return number not important
                } else {
                    state.removeLast()
                    val backDirection = when (state.last.lastSuccess) {
                        1L -> 2L
                        2L -> 1L
                        3L -> 4L
                        4L -> 3L
                        else -> throw RuntimeException("Unknown direction : ${state.last.lastSuccess}")
                    }
                    returning = true
                    backDirection
                }
            } else {
                point.currentDir
            }
            return lastInput
        }

        fun getPlane() = plane
    }

    private data class VisitPoint(val pos: Point, var currentDir: Long = 1L, var lastSuccess: Long = 0)
}