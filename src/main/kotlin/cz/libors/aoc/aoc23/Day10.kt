package cz.libors.aoc.aoc23

import cz.libors.util.*

@Day(name = "Pipe Maze")
object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input10.txt")
            .toPointsWithValue()
            .filter { it.second != '.' }
            .toMap()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Map<Point, Char>) = findLoop(input).size / 2

    private fun task2(input: Map<Point, Char>): Int {
        val path = findLoop(input)
        val box = path.boundingBox()
        val upPoint = path.first { it.y == box.first.y }
        val upIndex = path.indexOf(upPoint)
        val rightHanded = (path[(upIndex + 1) % path.size] == upPoint.plus(Vector.RIGHT))
                || (path[(upIndex - 1).posMod(path.size)] == upPoint.plus(Vector.LEFT))
        val innerLoopPath = if (rightHanded) path else path.reversed()
        val innerPoints = innerLoopPath.indices.flatMap { i ->
            rightHandRule(innerLoopPath[i], innerLoopPath[(i + 1) % innerLoopPath.size], innerLoopPath[(i + 2) % innerLoopPath.size])
        }.filter { !path.contains(it) }.toSet()
        return freeBodies(box, path.toSet()).filter { b -> b.any { innerPoints.contains(it) } }.sumOf { it.size }
    }

    private fun freeBodies(box: Pair<Point, Point>, path: Set<Point>): List<Set<Point>> {
        val freePoints = mutableSetOf<Point>()
        for (x in (box.first.x..box.second.x)) {
            for (y in (box.first.y..box.second.y)) {
                val p = Point(x, y)
                if (!path.contains(p)) {
                    freePoints.add(p)
                }
            }
        }
        val bodies = mutableListOf<Set<Point>>()
        for (p in freePoints) {
            if (!bodies.any { it.contains(p) }) {
                bodies.add(flood(p, freePoints))
            }
        }
        return bodies
    }

    private fun rightHandRule(p: Point, p2: Point, p3: Point): List<Point> = when (p.vectorTo(p2)) {
        Vector.RIGHT -> if (p2.vectorTo(p3) == Vector.UP) listOf(p.plus(Vector.DOWN), p2.plus(Vector.DOWN)) else listOf(p.plus(Vector.DOWN))
        Vector.LEFT -> if (p2.vectorTo(p3) == Vector.DOWN) listOf(p.plus(Vector.UP), p2.plus(Vector.UP)) else listOf(p.plus(Vector.UP))
        Vector.UP -> if (p2.vectorTo(p3) == Vector.LEFT) listOf(p.plus(Vector.RIGHT), p2.plus(Vector.RIGHT)) else listOf(p.plus(Vector.RIGHT))
        Vector.DOWN -> if (p2.vectorTo(p3) == Vector.RIGHT) listOf(p.plus(Vector.LEFT), p2.plus(Vector.LEFT)) else listOf(p.plus(Vector.LEFT))
        else -> throw IllegalArgumentException()
    }

    private fun findLoop(input: Map<Point, Char>): List<Point> {
        val start = input.entries.first { it.value == 'S' }.key
        val firstStep = start.adjacentPoints().map { Pair(it, input[it]) }
            .filter { it.second != null && it.second != '.' }
            .first { connections(it.first, it.second!!).contains(start) }.first
        val path = mutableListOf(firstStep)
        var point = firstStep
        var last = start
        while (point != start) {
            val x = connections(point, input[point]!!).first { it != last }
            last = point
            point = x
            path.add(point)
        }
        return path
    }

    private fun connections(p: Point, pipe: Char): List<Point> = when (pipe) {
        '|' -> listOf(p.plus(Vector.UP), p.plus(Vector.DOWN))
        '-' -> listOf(p.plus(Vector.LEFT), p.plus(Vector.RIGHT))
        'L' -> listOf(p.plus(Vector.UP), p.plus(Vector.RIGHT))
        'J' -> listOf(p.plus(Vector.UP), p.plus(Vector.LEFT))
        '7' -> listOf(p.plus(Vector.DOWN), p.plus(Vector.LEFT))
        'F' -> listOf(p.plus(Vector.DOWN), p.plus(Vector.RIGHT))
        else -> throw IllegalArgumentException()
    }
}