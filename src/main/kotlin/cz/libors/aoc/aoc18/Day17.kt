package cz.libors.aoc.aoc18

import cz.libors.util.*
import java.awt.Color

@Day("Reservoir Research")
object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input17.txt").map { line ->
            val nums = line.findInts()
            Def(nums[0], Pair(nums[1], nums[2]), line[0] == 'x')
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task2(input: List<Def>): Int {
        val clays = findClays(input)
        val waterMap = WaterFill(findClays(input)).start()
        val minY = clays.boundingBox().first.y
        return waterMap.count { it.value == '~' && it.key.y >= minY }
    }


    private fun task1(input: List<Def>): Int {
        val clays = findClays(input)
        val waterMap = WaterFill(findClays(input)).start()
        val minY = clays.boundingBox().first.y
        return waterMap.count { it.value == '~' || it.value == '|' && it.key.y >= minY }
    }

    class WaterFill(clays: Set<Point>, val spring: Point = Point(500, 0)) {
        private val map = clays.associateWith { '#' }.toMutableMap()
        private val stopY = clays.maxOf { it.y } + 1
        private val g = Graphics(
            delay = 5,
            window = Pair(Point(470, 0), Point(550, 40)),
            charOrder = "#|~@",
            colorSchema = ColorSchemas.staticColors(listOf(Color.GRAY, Color.CYAN, Color.BLUE, Color.RED)),
            displayLabels = false
        )
        private var frame = 0

        fun start(): Map<Point, Char> {
            val spring = Point(500, 0)
            drop(spring)
            return map
        }

        private fun show(current: Point) {
            //g.showChars(map + listOf(current).associateWith { '@' }, visiblePoint = current, title = frame.toString())
        }

        private fun drop(p: Point) {
            show(p)
            if (p.y == stopY) return
            map[p] = '|'
            if (isSand(p.down())) {
                drop(p.down())
            } else if (isDrop(p.down())) {
              // do nothing
            } else {
                fill(p)
            }
            if (map[p.down()] == '~') fill(p)
            show(p)
        }

        private fun isWall(x: Int, y: Int) = isWall(Point(x, y))
        private fun isWall(p: Point) = map[p] == '#'
        private fun isWallOrWater(x: Int, y: Int) = isWallOrWater(Point(x, y))
        private fun isWallOrWater(p: Point) = map[p] == '#' || map[p] == '~'
        private fun isSand(x: Int, y: Int) = isSand(Point(x, y))
        private fun isSand(p: Point) = map[p] == null
        private fun isDrop(p: Point) = map[p] == '|'

        private fun fill(p: Point) {
            show(p)
            var xl = p.x
            while (!isWall(xl - 1, p.y) && isWallOrWater(xl - 1, p.y + 1)) xl--
            var xr = p.x
            while (!isWall(xr + 1, p.y) && isWallOrWater(xr + 1, p.y + 1)) xr++
            val bounded = isWall(xr + 1, p.y) && isWall(xl - 1, p.y)
            if (bounded) {
                for (x in xl..xr) map[Point(x, p.y)] = '~'
            } else {
                for (x in xl..xr) map[Point(x, p.y)] = '|'
                if (isSand(xr + 1, p.y)) drop(Point(xr + 1, p.y))
                if (isSand(xl - 1, p.y)) drop(Point(xl - 1, p.y))
            }
        }
    }

    private fun findClays(defs: List<Def>): Set<Point> {
        val result = mutableSetOf<Point>()
        for (def in defs) {
            if (def.fixX) {
                for (y in def.range.first..def.range.second) result.add(Point(def.fix, y))
            } else {
                for (x in def.range.first..def.range.second) result.add(Point(x, def.fix))
            }
        }
        return result
    }

    private data class Def(val fix: Int, val range: Pair<Int, Int>, val fixX: Boolean)
}