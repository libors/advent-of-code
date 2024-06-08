package cz.libors.aoc.aoc23

import cz.libors.util.*

private typealias Maze = Map<Point, Char>

@Day(name = "The Floor Will Be Lava")
object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input16.txt").toPointsWithValue()
            .filter { it.second != '.' }
            .toMap()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(maze: Maze): Int {
        val box = maze.keys.boundingBox()
        val beam = Beam(maze)
        beam.go(box.first, Vector.RIGHT)
        return beam.energized.count()
    }

    private fun task2(maze: Maze): Int {
        val box = maze.keys.boundingBox()
        val beams = mutableListOf<Beam>()
        (box.first.x ..box.second.x).forEach {
            val topBeam = Beam(maze)
            topBeam.go(Point(it, box.first.y), Vector.DOWN)
            val bottomBeam = Beam(maze)
            bottomBeam.go(Point(it, box.second.y), Vector.UP)
            beams.add(topBeam)
            beams.add(bottomBeam)
        }
        (box.first.y ..box.second.y).forEach {
            val leftBeam = Beam(maze)
            leftBeam.go(Point(box.first.x, it), Vector.RIGHT)
            val rightBeam = Beam(maze)
            rightBeam.go(Point(box.second.x, it), Vector.LEFT)
            beams.add(leftBeam)
            beams.add(rightBeam)
        }
        return beams.maxOf { it.energized.count() }
    }

    private class Beam(val maze: Maze) {

        val energized = mutableSetOf<Point>()
        private val box = maze.keys.boundingBox()
        private val visited = mutableSetOf<Pair<Point, Vector>>()
        private val queue = mutableListOf<Pair<Point, Vector>>()

        fun go(p: Point, dir: Vector) {
            queue.add(Pair(p, dir))
            while (queue.isNotEmpty()) {
                val pair = queue.removeFirst()
                gox(pair.first, pair.second)
            }
        }

        private fun goTo(p: Point, dir: Vector) {
            queue.add(Pair(p.plus(dir), dir))
        }

        fun gox(p: Point, dir: Vector) {
            val pair = Pair(p, dir)
            if (visited.contains(pair)) return else visited.add(pair)
            if (!box.contains(p)) return
            energized.add(p)

            val tile = maze[p]
            when (tile) {
                '/' -> when (dir) {
                    Vector.UP, Vector.DOWN -> goTo(p, dir.turnRight())
                    Vector.LEFT, Vector.RIGHT -> goTo(p, dir.turnLeft())
                }
                '\\' -> when (dir) {
                    Vector.UP, Vector.DOWN -> goTo(p, dir.turnLeft())
                    Vector.LEFT, Vector.RIGHT -> goTo(p, dir.turnRight())
                }
                '|' -> if (dir.x == 0) goTo(p, dir) else {
                    goTo(p, dir.turnRight())
                    goTo(p, dir.turnLeft())
                }
                '-' -> if (dir.y == 0) go(p.plus(dir), dir) else {
                    goTo(p, dir.turnRight())
                    goTo(p, dir.turnLeft())
                }
                null -> goTo(p, dir)
            }
        }
    }
}