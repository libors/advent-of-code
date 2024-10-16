package cz.libors.aoc.aoc21

import cz.libors.util.*

@Day("Trick Shot")
object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val (minx, maxx, miny, maxy) = readToText("input17.txt").findInts()
        val box = Pair(Point(minx, miny), Point(maxx, maxy))
        val possibleX = findX(minx, maxx)
        val possibleY = findY(miny, maxy)
        val paths = possiblePaths(possibleX, possibleY, box)
        println(task1(paths))
        println(task2(paths))
    }

    private fun task1(paths: List<List<Point>>) = paths.flatten().maxOf { it.y }
    private fun task2(paths: List<List<Point>>) = paths.size

    private fun possiblePaths(possibleX: List<Int>, possibleY: List<Int>, box: Pair<Point, Point>): List<List<Point>> {
        val initials = mutableListOf<List<Point>>()
        for (x in possibleX)
            for (y in possibleY) {
                val path = simulate(box, Vector(x, y))
                if (path.isNotEmpty()) initials.add(path)
            }
        return initials
    }

    private fun simulate(targetBox: Pair<Point, Point>, initialSpeed: Vector): List<Point> {
        var x = 0
        var y = 0
        var vx = initialSpeed.x
        var vy = initialSpeed.y
        val xRange = targetBox.first.x..targetBox.second.x
        val yRange = targetBox.first.y..targetBox.second.y
        val result = mutableListOf<Point>()
        while (y >= yRange.first && x <= xRange.last) {
            x += vx
            y += vy
            result.add(Point(x, y))
            if (xRange.contains(x) && yRange.contains(y)) return result
            vy -= 1
            vx -= if (vx > 0) 1 else if (vx < 0) -1 else 0
        }
        return listOf()
    }

    private fun findY(min: Int, max: Int): List<Int> {
        val result = mutableListOf<Int>()
        for (initv in -1000..1000) {
            var y = 0
            var v = initv
            while (y >= max) {
                y += v
                v -= 1
                if (y in min..max) {
                    result.add(initv)
                    break
                }
            }
        }
        return result
    }

    private fun findX(min: Int, max: Int): List<Int> {
        val result = mutableListOf<Int>()
        for (initv in 0..max + 5) {
            var x = 0
            var v = initv
            while (x <= max || v >= 0) {
                x += v
                v -= 1
                if (x in min..max) {
                    result.add(initv)
                    break
                }
            }
        }
        return result
    }
}