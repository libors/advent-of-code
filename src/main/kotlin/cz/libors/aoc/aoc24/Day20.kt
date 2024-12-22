package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("Race Condition")
object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input20.txt").toPointsWithValue().toMap().toMutableMap()
        val start = input.filter { it.value == 'S' }.keys.first()
        val end = input.filter { it.value == 'E' }.keys.first()
        val maze = input.filter { it.value == '.' || it.value == 'S' || it.value == 'E'}.keys

        println(task1(maze, start, end))
        println(task2(maze, start, end))
    }

    private fun task1(maze: Set<Point>, start: Point, end: Point) = countCheats(maze, start, end, 2)
    private fun task2(maze: Set<Point>, start: Point, end: Point) = countCheats(maze, start, end, 20)

    private fun countCheats(maze: Set<Point>, start: Point, end: Point, cheatTime: Int): Int {
        val noCheatScore = bfs(start, { it == end }) { it.neighbours().filter { n -> maze.contains(n) }}.getScore()!!
        val distToEnd = bfsToAll(end, { it.neighbours().filter { n -> maze.contains(n) } }).distances()
        val distFromStart = bfsToAll(start, { it.neighbours().filter { n -> maze.contains(n) } }).distances()
        var cnt = 0
        for (source in maze) {
            for (xdiff in -cheatTime..cheatTime) {
                for (ydiff in -cheatTime..cheatTime) {
                    val target = Point(source.x + xdiff, source.y + ydiff)
                    if (maze.contains(target)) {
                        val cheatDist = source.manhattanDistance(target)
                        if (cheatDist <= cheatTime) {
                            val newScore = distFromStart[source]!! + cheatDist + distToEnd[target]!!
                            val savedScore = noCheatScore - newScore
                            if (savedScore >= 100) cnt++
                        }
                    }
                }
            }
        }
        return cnt
    }
}