package cz.libors.aoc.aoc24

import cz.libors.util.*

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
        val distancesToEnd = bfsToAll(end, { it.neighbours().filter { n -> maze.contains(n) } }).distances()
        val distancesFromStart = bfsToAll(start, { it.neighbours().filter { n -> maze.contains(n) } }).distances()
        var cnt = 0
        for (source in maze) {
            for (x in source.x - cheatTime..source.x + cheatTime) {
                for (y in source.y - cheatTime..source.y + cheatTime) {
                    val target = Point(x, y)
                    if (maze.contains(target)) {
                        val dist = source.manhattanDistance(target)
                        if (dist <= cheatTime) {
                            val newScore = distancesFromStart[source]!! + distancesToEnd[target]!! + dist
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