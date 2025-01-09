package cz.libors.aoc.aoc24

import cz.libors.util.*
import cz.libors.util.Vector
import java.awt.Color
import java.util.*

@Day("Warehouse Woes")
object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input15.txt").splitByEmptyLine()
        val moves = input[1].map { Vector.from(it.toString(), mandatory = false) }.filterNotNull()
        val maze = input[0].splitByNewLine().toPointsWithValue().toMap().toMutableMap()
        println(task1(maze, moves))
        val wideMaze = input[0].splitByNewLine().map { widenMazeLine(it) }.toPointsWithValue().toMap().toMutableMap()
        println(task2(wideMaze, moves))
    }

    private fun widenMazeLine(line: String) = line.map {
        when (it) {
            '@' -> "@."
            'O' -> "[]"
            else -> "$it$it"
        }
    }.joinToString("")

    private fun replaceStart(maze: MutableMap<Point, Char>): Point = maze.filter { it.value == '@' }.keys.first()
        .also { maze.remove(it) }

    private fun countScore(maze: Map<Point, Char>, char: Char) =
        maze.filter { it.value == char }.keys.sumOf { it.x + it.y * 100 }

    private fun task1(maze: MutableMap<Point, Char>, moves: List<Vector>): Int {
        moveInMaze(maze, moves, replaceStart(maze), ::push)
        return countScore(maze, 'O')
    }

    private fun task2(maze: MutableMap<Point, Char>, moves: List<Vector>): Int {
        moveInMaze(maze, moves, replaceStart(maze), ::widePush)
        return countScore(maze, '[')
    }

    private fun moveInMaze(
        maze: MutableMap<Point, Char>, moves: List<Vector>, start: Point,
        pushFn: (MutableMap<Point, Char>, Point, Vector) -> Point
    ) {
        val g = initGraphics()
        var pos = start
        for (dir in moves) {
            val nextPos = pos + dir
            pos = when (maze[nextPos]) {
                null -> nextPos
                '#' -> pos
                else -> pushFn(maze, pos, dir)
            }
            // g.showChars(maze.mapValues { if (it.value == null) ' ' else it.value } + mapOf(pos to '@'), visiblePoint = pos)
        }
    }

    private fun push(maze: MutableMap<Point, Char>, pos: Point, dir: Vector): Point {
        val nextPos = pos + dir
        var check = nextPos
        while (maze[check] == 'O') check += dir
        return if (maze[check] != '#') {
            maze[check] = 'O'
            maze.remove(nextPos)
            nextPos
        } else {
            pos
        }
    }

    private fun widePush(maze: MutableMap<Point, Char>, pos: Point, dir: Vector): Point {
        val next = pos + dir
        if (dir in listOf(Vector.LEFT, Vector.RIGHT)) {
            return wideMoveLeftRight(next, maze, dir, pos)
        } else {
            val boxes = findBoxes(pos, dir, maze)
            if (boxes.isEmpty()) return pos
            moveBoxes(boxes, dir, maze)
            return next
        }
    }

    private fun wideMoveLeftRight(next: Point, maze: MutableMap<Point, Char>, move: Vector, pos: Point): Point {
        var check = next
        while (maze[check] in listOf('[', ']')) check += move
        return if (maze[check] == '#') {
            pos
        } else {
            var cpos = check
            while (cpos != next) {
                val prev = cpos - move
                maze[cpos] = maze[prev]!!
                cpos = prev
            }
            maze.remove(next)
            next
        }
    }

    private fun moveBoxes(boxes: Set<Point>, dir: Vector, maze: MutableMap<Point, Char>) {
        val sorted = boxes.toList().sortedBy { if (dir == Vector.UP) it.y else -it.y }
        for (box in sorted) {
            maze[box + dir] = maze[box]!!
            maze[box.right() + dir] = maze[box.right()]!!
            maze.remove(box)
            maze.remove(box.right())
        }
    }

    private fun initGraphics() = Graphics(
        debugFromStart = false, delay = 60, labelColor = Color.BLACK, charOrder = "@# O[]", window = Box(Point(0, 0), Point(30, 15)),
        colorSchema = ColorSchemas.staticColors(listOf(Color.RED, Color.LIGHT_GRAY, Color.WHITE), default = Color.YELLOW), adventTheme = false,
    )

    private fun findBoxes(pos: Point, dir: Vector, maze: Map<Point, Char>): Set<Point> {
        val boxes = mutableSetOf<Point>()
        val q = LinkedList<Point>()
        q.add(pos)
        while (q.isNotEmpty()) {
            val cur = q.removeFirst()
            val nextPos = cur + dir
            when (maze[nextPos]) {
                '[' -> {
                    boxes.add(nextPos)
                    q.addAll(listOf(nextPos, nextPos.right()))
                }
                ']' -> {
                    boxes.add(nextPos.left())
                    q.addAll(listOf(nextPos, nextPos.left()))
                }
                '#' -> return setOf()
            }
        }
        return boxes
    }
}