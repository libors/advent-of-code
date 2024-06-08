package cz.libors.aoc.aoc23

import cz.libors.util.*

private typealias Rocks = List<Pair<Point, Char>>

@Day(name = "Parabolic Reflector Dish")
object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input14.txt").toPointsWithValue().filter { it.second != '.' }

        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Rocks) = valueOnNorth(roll(input, Vector.UP))

    private fun task2(input: List<Pair<Point, Char>>): Int {
        val cycles = 1000000000
        val period = getPeriod(input)
        val todoCycles = (cycles - period.second) % (period.first - period.second)
        val totalCycles = todoCycles + period.second
        var x = input
        for (i in 1..totalCycles) x = cycle(x)
        return valueOnNorth(x)
    }

    private fun getPeriod(rocks: Rocks): Pair<Int, Int> {
        val cache = mutableMapOf<Rocks, Int>()
        var x = rocks
        var cycle = 0
        while(true) {
            x = cycle(x)
            cycle++
            if (cache.contains(x)) return Pair(cycle, cache[x]!!) else cache[x] = cycle
        }
    }

    private fun cycle(rocks: Rocks): Rocks {
        var x = rocks
        x = roll(x, Vector.UP)
        x = roll(x, Vector.LEFT)
        x = roll(x, Vector.DOWN)
        x = roll(x, Vector.RIGHT)
        return x
    }

    private fun valueOnNorth(input: Rocks): Int {
        val box = input.map { it.first }.boundingBox()
        val size = box.second.y - box.first.y + 1

        return input.filter { it.second == 'O' }.sumOf { size - it.first.y }
    }

    private fun roll(rocks: Rocks, where: Vector): Rocks {
        val box = rocks.map { it.first }.boundingBox()
        val result = mutableListOf<Pair<Point, Char>>()
        result.addAll(rocks.filter { it.second == '#' })
        when (where) {
            Vector.UP ->
                for (x in box.first.x..box.second.x) {
                    val columnRocks = rocks.filter { it.first.x == x }.sortedBy { it.first.y }
                    var pos = box.first.x
                    for (rock in columnRocks) {
                        if (rock.second == 'O') result.add(Point(x, pos++) to 'O') else pos = rock.first.y + 1
                    }
                }
            Vector.DOWN ->
                for (x in box.first.x..box.second.x) {
                    val columnRocks = rocks.filter { it.first.x == x }.sortedBy { it.first.y }.reversed()
                    var pos = box.second.x
                    for (rock in columnRocks) {
                        if (rock.second == 'O') result.add(Point(x, pos--) to 'O') else pos = rock.first.y - 1
                    }
                }
            Vector.LEFT ->
                for (y in box.first.y..box.second.y) {
                    val columnRocks = rocks.filter { it.first.y == y }.sortedBy { it.first.x }
                    var pos = box.first.y
                    for (rock in columnRocks) {
                        if (rock.second == 'O') result.add(Point(pos++, y) to 'O') else pos = rock.first.x + 1
                    }
                }
            Vector.RIGHT ->
                for (y in box.first.y..box.second.y) {
                    val columnRocks = rocks.filter { it.first.y == y }.sortedBy { it.first.x }.reversed()
                    var pos = box.second.y
                    for (rock in columnRocks) {
                        if (rock.second == 'O') result.add(Point(pos--, y) to 'O') else pos = rock.first.x - 1
                    }
                }
        }
        return result
    }

}