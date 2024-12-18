package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("Reindeer Maze")
object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input16.txt").toPointsWithValue().toMap().toMutableMap()
        val start = input.filter { it.value == 'S' }.keys.first()
        val end = input.filter { it.value == 'E' }.keys.first()
        input[start] = '.'
        input[end] = '.'

        val best = task1(input, start, end)
        println(best)
        println(task2(input, start, end, best))
    }

    private fun task1(maze: Map<Point, Char>, start: Point, end: Point) =
        shortestPaths(maze, Pair(start, Vector.RIGHT), end).pathTo(Pair(end, Vector.RIGHT)).getScore()!!

    private fun task2(maze: Map<Point, Char>, start: Point, end: Point, best: Int) =
        Finder(maze, start, end, best).find()

    private fun shortestPaths(maze: Map<Point, Char>, start: Pair<Point, Vector>, freeTurn: Point) =
        dijkstraToAll(start,
            neighboursFn = { (pos, dir) ->
                val result = mutableListOf(Pair(pos, dir.turnRight()), Pair(pos, dir.turnLeft()))
                if (maze[pos + dir] == '.') result.add(Pair(pos + dir, dir))
                result
            },
            distanceFn = { a, b -> if (a.second == b.second) 1 else if (a.first == freeTurn) 0 else 1000 })

    // count distances from (pos, dir) to end to stop DFS branches when they got too long
    private fun minDistancesToEnd(maze: Map<Point, Char>, end: Point) =
        shortestPaths(maze, Pair(end, Vector.RIGHT), end).distances()
            .mapKeys { (k, _) -> Pair(k.first, -k.second) }

    private class Finder(val maze: Map<Point, Char>, val start: Point, val end: Point, val bestScore: Int) {
        private val bestNodes = mutableSetOf(start, end)
        private val visited = mutableSetOf(start)
        private val endDistMap = minDistancesToEnd(maze, end)

        fun find(): Int {
            dfs(start, Vector.RIGHT, 0)
//            Graphics(charOrder = "#.@", colorSchema = ColorSchemas.staticColors(listOf(Color.GRAY, Color.WHITE, Color.ORANGE)))
//                .showChars(maze + bestNodes.associateWith { '@' })
            return bestNodes.size
        }

        private fun dfs(pos: Point, dir: Vector, score: Int) {
            if (pos == end) {
                if (score == bestScore) bestNodes.addAll(visited)
                return
            }
            val distToEnd = endDistMap[Pair(pos, dir)]
            if (distToEnd == null || score + distToEnd > bestScore) return
            val options = listOf(
                Option(pos + dir, dir, 1),
                Option(pos + dir.turnRight(), dir.turnRight(), 1001),
                Option(pos + dir.turnLeft(), dir.turnLeft(), 1001)
            )
            for (option in options) {
                if (maze[option.pos] == '.' && !visited.contains(option.pos)) {
                    visited.add(option.pos)
                    dfs(option.pos, option.dir, score + option.addScore)
                    visited.remove(option.pos)
                }
            }
        }

        private data class Option(val pos: Point, val dir: Vector, val addScore: Int)
    }
}