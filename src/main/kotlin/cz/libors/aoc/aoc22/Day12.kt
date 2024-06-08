package cz.libors.aoc.aoc22

import cz.libors.util.Day
import cz.libors.util.readToLines
import java.util.*

@Day(name = "Hill Climbing Algorithm")
object Day12 {
    private const val START = 'S' - 'a'
    private const val END = 'E' - 'a'

    private fun task1(input: List<List<Int>>): Int {
        val start = findCoords(input, START)
        val end = findCoords(input, END)
        val land = replaceStartAndMarks(input)
        return findPath(land, start, end)
    }

    private fun task2(input: List<List<Int>>): Int {
        val end = findCoords(input, END)
        val land = replaceStartAndMarks(input)
        val starts = mutableListOf<Coords>()
        for (row in land.indices)
            for (col in land[0].indices)
                if (land[row][col] == 0)
                    starts.add(Coords(col, row))
        return starts.minOf { findPath(land, it, end) }
    }

    private fun replaceStartAndMarks(input: List<List<Int>>) = input.map { row ->
        row.map {
            when (it) {
                START -> 0
                END -> 'z' - 'a'
                else -> it
            }
        }
    }

    private fun findPath(land: List<List<Int>>, start: Coords, end: Coords): Int {
        val visited = mutableSetOf<Coords>()
        val queue: Deque<State> = LinkedList()
        queue.addLast(State(start, 0))
        visited.add(start)
        do {
            if (queue.isEmpty()) return Int.MAX_VALUE
            val state = queue.removeFirst()
            if (state.coords == end)
                return state.length
            for (coord in generatePath(state.coords, land)) {
                if (!visited.contains(coord)) {
                    queue.addLast(State(coord, state.length + 1))
                    visited.add(coord)
                }
            }
        } while (true)
    }

    private fun generatePath(coords: Coords, land: List<List<Int>>): List<Coords> {
        val result = mutableListOf<Coords>()
        val x = coords.col
        val y = coords.row
        val maxHeight = land[y][x].let { if (it == START) 0 else it } + 1
        if (x > 0 && land[y][x - 1] <= maxHeight)
            result.add(Coords(x - 1, y))
        if (x < land[0].size - 1 && land[y][x + 1] <= maxHeight)
            result.add(Coords(x + 1, y))
        if (y > 0 && land[y - 1][x] <= maxHeight)
            result.add(Coords(x, y - 1))
        if (y < land.size - 1 && land[y + 1][x] <= maxHeight)
            result.add(Coords(x, y + 1))
        return result
    }

    private fun findCoords(land: List<List<Int>>, height: Int): Coords {
        for (row in land.indices)
            for (col in land[0].indices)
                if (land[row][col] == height) return Coords(col, row)
        throw java.lang.IllegalArgumentException()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input12.txt")
            .map { line -> line.map { char -> (char - 'a') } }

        println(task1(input))
        println(task2(input))
    }

    data class Coords(val col: Int, val row: Int)
    data class State(val coords: Coords, val length: Int)
}