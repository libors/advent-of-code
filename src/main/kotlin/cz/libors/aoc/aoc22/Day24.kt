package cz.libors.aoc.aoc22

import cz.libors.util.*
import java.lang.IllegalArgumentException
import java.util.LinkedList

@Day(name = "Blizzard Basin")
object Day24 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input24.txt")
        val size = Point(input[0].length - 2, input.size - 2)
        val entryPoint = Point(input[0].indexOf('.') - 1, -1)
        val exitPoint = Point(input[input.size - 1].indexOf('.') - 1, size.y)
        val entryExit = Pair(entryPoint, exitPoint)

        val startWing = input.subList(1, input.size - 1).flatMapIndexed { rowIdx, row ->
            row.toCharArray().toList().subList(1, row.length - 1)
                .mapIndexed { colIdx, col -> Point(colIdx, rowIdx) to Vector.from(col.toString()) }
                .filter { it.second != null } as List<Pair<Point, Vector>>
        }

        val valley = Valley(startWing, size, entryExit)
        println(task1(valley, entryExit))
        println(task2(valley, entryExit))
    }

    private fun task1(valley: Valley, entryExit: Pair<Point, Point>) =
        bfs(entryExit.first, entryExit.second, valley, 0)

    private fun task2(valley: Valley, entryExit: Pair<Point, Point>): Int {
        val goToExitMin = bfs(entryExit.first, entryExit.second, valley, 0)
        val goForSnackMin = bfs(entryExit.second, entryExit.first, valley, goToExitMin)
        return bfs(entryExit.first, entryExit.second, valley, goForSnackMin)
    }

    private fun bfs(entryPoint: Point, exitPoint: Point, valley: Valley, minute: Int): Int {
        val queue = LinkedList<QueRecord>()
        queue.addLast(QueRecord(minute, entryPoint))
        val visited = mutableSetOf<QueRecord>()
        while (true) {
            val record = queue.removeFirst()
            if (visited.contains(record))
                continue
            if (record.position == exitPoint)
                return record.minute
            val nextMinuteEmpty = valley.getForMinute(record.minute + 1)
            record.position.neighbours()
                .filter { nextMinuteEmpty.contains(it) }
                .forEach { queue.addLast(QueRecord(record.minute + 1, it)) }
            if (nextMinuteEmpty.contains(record.position))
                queue.addLast(QueRecord(record.minute + 1, record.position))
            visited.add(record)
        }
    }

    private data class QueRecord(val minute: Int, val position: Point)

    private class Valley(startWind: List<Pair<Point, Vector>>, val size: Point, val entryExit: Pair<Point, Point>) {
        val wind = mutableListOf<Position>()

        init {
            wind.add(Position(startWind, setOf()))
        }

        fun getForMinute(minute: Int): Set<Point> {
            if (minute < wind.size) return wind[minute].empty
            if (minute > wind.size) throw IllegalArgumentException()
            computeNextMinute()
            return wind.last().empty
        }

        private fun computeNextMinute() {
            val updatedWind = wind.last().wind.map {
                val newPoint = it.first + it.second
                val newPointMod = Point(newPoint.x.posMod(size.x), newPoint.y.posMod(size.y))
                Pair(newPointMod, it.second)
            }
            val windPresent = updatedWind.map { it.first }.toSet()
            val emptyPoints = mutableSetOf(entryExit.first, entryExit.second)
            for (col in 0 until size.x)
                for (row in 0 until size.y) {
                    val p = Point(col, row)
                    if (!windPresent.contains(p)) emptyPoints.add(p)
                }
            wind.add(Position(updatedWind, emptyPoints))
        }

        private data class Position(val wind: List<Pair<Point, Vector>>, val empty: Set<Point>)
    }
}