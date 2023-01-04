package cz.libors.aoc.aoc22

import cz.libors.util.*

object Day22 {

    private val directions = listOf(Vector.UP, Vector.RIGHT, Vector.DOWN, Vector.LEFT)

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input22.txt").splitByEmptyLine()
        val map = input[0].splitByNewLine()
            .flatMapIndexed { lineIdx, line ->
                line.toCharArray().mapIndexed { charIdx, char -> Point(charIdx + 1, lineIdx + 1) to char }
            }
        val walls = map.filter { it.second == '#' }.map { it.first }.toSet()
        val road = map.filter { it.second == '.' }.map { it.first }.toSet()

        val moves = Regex("([0-9]+|[RL])")
            .findAll(input[1])
            .map { it.value }
            .toList()

        println(task1(moves, road, walls))
        println(task2(moves, road, walls))
    }

    private fun task1(moves: List<String>, road: Set<Point>, walls: Set<Point>): Int {
        val all = walls + road
        val lrLimits = all.groupBy { it.y }.mapValues { e -> Pair(e.value.minOf { it.x }, e.value.maxOf { it.x }) }
        val udLimits = all.groupBy { it.x }.mapValues { e -> Pair(e.value.minOf { it.y }, e.value.maxOf { it.y }) }

        val lrTransfers = mutableMapOf<Point, Pair<Point, Vector>>()
        val udTransfers = mutableMapOf<Point, Pair<Point, Vector>>()
        udLimits.forEach {
            udTransfers[Point(it.key, it.value.first - 1)] = Pair(Point(it.key, it.value.second), Vector.UP)
            udTransfers[Point(it.key, it.value.second + 1)] = Pair(Point(it.key, it.value.first), Vector.DOWN)
        }
        lrLimits.forEach {
            lrTransfers[Point(it.value.first - 1, it.key)] = Pair(Point(it.value.second, it.key), Vector.LEFT)
            lrTransfers[Point(it.value.second + 1, it.key)] = Pair(Point(it.value.first, it.key), Vector.RIGHT)
        }
        return followPath(moves, road, walls, lrTransfers, udTransfers)
    }

    private fun task2(moves: List<String>, road: Set<Point>, walls: Set<Point>): Int {
        val c = TransfersComputer(50)
        c.add(Point(2, 1), Vector.LEFT, Point(1, 3), Vector.LEFT, reverse = true) // A
        c.add(Point(2, 2), Vector.LEFT, Point(1, 3), Vector.UP) // B
        c.add(Point(2, 1), Vector.UP, Point(1, 4), Vector.LEFT) // C
        c.add(Point(3, 1), Vector.UP, Point(1, 4), Vector.DOWN) // D
        c.add(Point(3, 1), Vector.RIGHT, Point(2, 3), Vector.RIGHT, reverse = true) // E
        c.add(Point(3, 1), Vector.DOWN, Point(2, 2), Vector.RIGHT) // F
        c.add(Point(2, 3), Vector.DOWN, Point(1, 4), Vector.RIGHT) // G

//        val c = TransfersComputer(4)
//        c.add(Point(3, 1), Vector.UP, Point(1, 2), Vector.UP, reverse = true) // A
//        c.add(Point(3, 1), Vector.RIGHT, Point(4, 3), Vector.RIGHT, reverse = true) // B
//        c.add(Point(3, 1), Vector.LEFT, Point(2, 2), Vector.UP) // C
//        c.add(Point(3, 2), Vector.RIGHT, Point(4, 3), Vector.UP, reverse = true) // D
//        c.add(Point(1, 2), Vector.LEFT, Point(4, 3), Vector.DOWN, reverse = true) // E
//        c.add(Point(1, 2), Vector.DOWN, Point(3, 3), Vector.DOWN, reverse = true) // F
//        c.add(Point(2, 2), Vector.DOWN, Point(3, 3), Vector.LEFT, reverse = true) // G

        return followPath(moves, road, walls, c.lr, c.ud)
    }

    private class TransfersComputer(val size: Int) {
        val lr = mutableMapOf<Point, Pair<Point, Vector>>()
        val ud = mutableMapOf<Point, Pair<Point, Vector>>()

        fun add(qFrom: Point, dirFrom: Vector, qTo: Point, dirTo: Vector, reverse: Boolean = false) {
            //println()
            addInternal(qFrom, dirFrom, qTo, dirTo, reverse)
            addInternal(qTo, dirTo, qFrom, dirFrom, reverse)
        }

        private fun addInternal(qFrom: Point, dirFrom: Vector, qTo: Point, dirTo: Vector, reverse: Boolean) {
            for (i in 1..size) {
                val fx = qFrom.x - 1
                val fy = qFrom.y - 1
                val from = when (dirFrom) {
                    Vector.LEFT -> Point(fx * size, fy * size + i)
                    Vector.RIGHT -> Point((fx + 1) * size + 1, fy * size + i)
                    Vector.UP -> Point(fx * size + i, fy * size)
                    else -> Point(fx * size + i, (fy + 1) * size + 1)
                }
                val j = if (reverse) size + 1 - i else i
                val tx = qTo.x - 1
                val ty = qTo.y - 1
                val to = when (dirTo) {
                    Vector.LEFT -> Point(tx * size + 1, ty * size + j)
                    Vector.RIGHT -> Point((tx + 1) * size, ty * size + j)
                    Vector.UP -> Point(tx * size + j, ty * size + 1)
                    else -> Point(tx * size + j, (ty + 1) * size)
                }
                //println("$from -> $to")
                val toPair = Pair(to, dirTo.negative())
                if (dirFrom.x == 0) ud[from] = toPair else lr[from] = toPair
            }
        }
    }

    private fun followPath(
        moves: List<String>,
        road: Set<Point>,
        walls: Set<Point>,
        lrTransfers: Map<Point, Pair<Point, Vector>>,
        udTransfers: Map<Point, Pair<Point, Vector>>
    ): Int {
        var position = road.minOf { it.y }.let { minY -> road.filter { it.y == minY }.minByOrNull { it.x } }!!
        var dirIdx = 1
        moves.forEach {
            if (it == "R") {
                dirIdx++
            } else if (it == "L") {
                dirIdx--
            } else {
                val steps = it.toInt()
                for (i in 1..steps) {
                    val direction = directions[dirIdx.posMod(directions.size)]

                    var next = position + direction
                    val transfer = if (direction.y == 0) lrTransfers[next] else udTransfers[next]
                    if (transfer != null) {
                        next = transfer.first
                    }
                    if (road.contains(next)) {
                        position = next
                        if (transfer != null)
                            dirIdx = directions.indexOf(transfer.second)
                    } else if (walls.contains(next)) {
                        break
                    }
                }
            }
        }
        val facing = (dirIdx - 1).posMod(directions.size)
        return 1000 * position.y + 4 * position.x + facing
    }
}