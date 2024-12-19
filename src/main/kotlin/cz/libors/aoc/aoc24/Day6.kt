package cz.libors.aoc.aoc24

import cz.libors.util.*
import kotlin.time.measureTime

private typealias Visited = Pair<Point, Vector>

@Day("Guard Gallivant")
object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input6.txt").toPointsWithValue().toMap()
        val guardPos = input.filter { it.value != '.' && it.value != '#' }.map { it.key }.first()
        val guardDir = Vector.from(input[guardPos].toString())!!

        println(task1(input, guardPos, guardDir))
        println(task2(input, guardPos, guardDir))
    }

    private fun task1(map: Map<Point, Char>, start: Point, startDir: Vector) = go(map, start, startDir).visited.distinct().count()

    private fun task2(map: Map<Point, Char>, start: Point, startDir: Vector): Int {
        val newMap = map.toMutableMap()
        var loops = 0
        val possibleObstacles = go(map, start, startDir).visited
        val alreadyObstacles = mutableSetOf<Point>()
        for (i in 1 until possibleObstacles.size) {
            val obstacle = possibleObstacles[i]
            if (alreadyObstacles.contains(obstacle)) {continue}
            alreadyObstacles.add(obstacle)
            newMap[obstacle] = '#'
            val cycleStartFrom = possibleObstacles[i - 1]
            val cycleStartDir = possibleObstacles[i - 1].vectorTo(possibleObstacles[i])
            if (go(newMap, cycleStartFrom, cycleStartDir).isCycle) loops++
            newMap[obstacle] = '.'
        }
        return loops
    }

    private fun go(map: Map<Point, Char>, start: Point, startDir: Vector): GoResult {
        val visited = mutableSetOf<Pair<Point, Vector>>()
        val pointsOnPath = mutableListOf<Point>()
        var guardPos = start
        var guardDir = startDir
        while (true) {
            val state = Pair(guardPos, guardDir)
            if (visited.contains(state)) return GoResult(true, pointsOnPath)
            visited.add(state)
            if (pointsOnPath.isEmpty() || pointsOnPath.last() != guardPos) {
                pointsOnPath.add(guardPos)
            }
            val next = guardPos + guardDir
            when (map[next]) {
                null -> return GoResult(false, pointsOnPath)
                '#' -> guardDir = guardDir.turnRight()
                else -> guardPos += guardDir
            }
        }
    }

    private data class GoResult(val isCycle: Boolean, val visited: List<Point>)
}