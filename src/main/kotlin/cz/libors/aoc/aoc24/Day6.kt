package cz.libors.aoc.aoc24

import cz.libors.util.*

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

    private fun task1(map: Map<Point, Char>, start: Point, startDir: Vector) = go(map, start, startDir)
        .second.map { it.first }.distinct().count()

    private fun task2(map: Map<Point, Char>, start: Point, startDir: Vector): Int {
        val newMap = map.toMutableMap()
        var loops = 0
        val possibleObstacles = go(map, start, startDir).second.map { it.first }.distinct() - start
        for (obstacle in possibleObstacles) {
            newMap[obstacle] = '#'
            if (go(newMap, start, startDir).first) loops++
            newMap[obstacle] = '.'
        }
        return loops
    }

    // returns <isCycle, visitedNodes>
    private fun go(map: Map<Point, Char>, start: Point, startDir: Vector): Pair<Boolean, Set<Visited>> {
        val visited = mutableSetOf<Pair<Point, Vector>>()
        var guardPos = start
        var guardDir = startDir
        while (true) {
            val state = Pair(guardPos, guardDir)
            if (visited.contains(state)) return Pair(true, visited)
            visited.add(state)
            val next = guardPos + guardDir
            when (map[next]) {
                null -> return Pair(false, visited)
                '#' -> guardDir = guardDir.turnRight()
                else -> guardPos += guardDir
            }
        }
    }

}