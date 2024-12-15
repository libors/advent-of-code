package cz.libors.aoc.aoc24

import cz.libors.util.*
import cz.libors.util.Vector
import java.awt.Color
import java.util.*

object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input15.txt").splitByEmptyLine()
        val moves = input[1].map { Vector.from(it.toString()) }.filterNotNull()
        val maze = input[0].splitByNewLine().toPointsWithValue().toMap()
        println(task1(maze, moves))
        val wideMaze = input[0].splitByNewLine().map { widenMazeLine(it) }.toPointsWithValue().toMap()
        println(task2(wideMaze, moves))
    }

    private fun widenMazeLine(line: String) = line.map {
        when (it) {
            '@' -> "@."
            'O' -> "[]"
            else -> "$it$it"
        }
    }.joinToString(separator = "")

    private fun task1(input: Map<Point, Char>, moves: List<Vector>): Int {
        val maze = input.toMutableMap()
        var pos = maze.filter { it.value == '@' }.keys.first()
        maze[pos] = '.'
        val g = initGraphics()
        for (move in moves) {
            val nextPos = pos + move
            when (maze[nextPos]) {
                '.' -> pos = nextPos
                'O' -> {
                    var check = nextPos
                    while (maze[check] == 'O') check += move
                    if (maze[check] != '#') {
                        maze[check] = 'O'
                        maze[nextPos] = '.'
                        pos = nextPos
                    }
                }
            }
            //showMaze(g, pos, maze)
        }
        val start = maze.keys.boundingBox().first
        return maze.filter { it.value == 'O' }.keys.sumOf { it.x - start.x + (it.y - start.y) * 100 }
    }

    private fun task2(input: Map<Point, Char>, moves: List<Vector>): Int {
        val maze = input.toMutableMap()
        var pos = maze.filter { it.value == '@' }.keys.first()
        maze[pos] = '.'
        val g = initGraphics()
        for (move in moves) {
            val next = pos + move
            val nextVal = maze[next]
            if (nextVal == '.') {
                pos = next
            } else if (nextVal == '#') {
                // nothing
            } else {
                if (move in listOf(Vector.LEFT, Vector.RIGHT)) {
                    pos = wideMoveLeftRight(next, maze, move, pos)
                } else {
                    val boxes = findBoxes(pos, move, maze)
                    if (boxes.isNotEmpty()) {
                        moveBoxes(boxes, move, maze)
                        pos = next
                    }
                }
            }
            //showMaze(g, pos, maze)
        }
        val start = maze.keys.boundingBox().first
        return maze.filter { it.value == '[' }.keys.map { it.x - start.x + (it.y - start.y) * 100 }.sum()
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
            maze[next] = '.'
            next
        }
    }

    private fun moveBoxes(boxes: Set<Point>, move: Vector, maze: MutableMap<Point, Char>) {
        val sorted = boxes.toList().sortedBy { if (move == Vector.UP) it.y else -it.y }
        for (box in sorted) {
            maze[box + move] = maze[box]!!
            maze[box.right() + move] = maze[box.right()]!!
            maze[box] = '.'
            maze[box.right()] = '.'
        }
    }

    private fun initGraphics() = Graphics(
        debugFromStart = false,
        delay = 50,
        labelColor = Color.BLACK,
        colorSchema = ColorSchemas.staticColors(listOf(Color.RED, Color.LIGHT_GRAY, Color.WHITE, Color.YELLOW, Color.YELLOW, Color.YELLOW))
    )

    private fun showMaze(g: Graphics, pos: Point, maze: Map<Point, Char>) =
        g.showChars(maze.mapValues { if (it.value == '.') ' ' else it.value } + mapOf(pos to '@'), order = "@# o[]")

    private fun findBoxes(pos: Point, move: Vector, maze: Map<Point, Char>): Set<Point> {
        val boxes = mutableSetOf<Point>()
        val q = LinkedList<Point>()
        q.add(pos)
        while (q.isNotEmpty()) {
            val cur = q.removeFirst()
            val nextBrick = cur + move
            when (maze[nextBrick]) {
                '[' -> {
                    boxes.add(nextBrick)
                    q.addAll(listOf(nextBrick, nextBrick.right()))
                }
                ']' -> {
                    boxes.add(nextBrick.left())
                    q.addAll(listOf(nextBrick, nextBrick.left()))
                }
                '#' -> return setOf()
            }
        }
        return boxes
    }
}