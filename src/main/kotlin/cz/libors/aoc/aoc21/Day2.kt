package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.Point
import cz.libors.util.Vector
import cz.libors.util.readToLines

@Day("Dive!")
object Day2 {

    data class Item(val v: Vector, val num: Int)

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input2.txt").map {
            val split = it.split(' ')
            val v = when(split[0]) {
                "forward" -> Vector.RIGHT
                "down" -> Vector.DOWN
                "up" -> Vector.UP
                else -> throw IllegalArgumentException("Invalid input")
            }
            Item(v, split[1].toInt())
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(items: List<Item>): Int {
        var p = Point(0, 0)
        for (i in items) {
            p += i.v * i.num
        }
        return p.x * p.y
    }

    private fun task2(items: List<Item>): Int {
        var aim = 0
        var hor = 0
        var depth = 0
        for (i in items) {
            when(i.v) {
                Vector.RIGHT -> {
                    hor += i.num
                    depth += i.num * aim
                }
                Vector.DOWN -> aim += i.num
                Vector.UP -> aim -= i.num
            }
        }
        return hor * depth
    }
}