package cz.libors.aoc.aoc23

import cz.libors.util.*
import cz.libors.util.Vector
import java.math.BigInteger
import java.util.*

@Day(name = "Lavaduct Lagoon")
object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input18.txt")
        val task1Instructions = input.map {
            val value = it.split(" ")
            Instruction(Vector.from(value[0])!!, value[1].toInt())
        }
        println(task1(task1Instructions))

        val task2Instructions = input.map {
            val value = it.substringAfter("#").substringBefore(")")
            val vector = when(value[5]) {
                '0' -> Vector.RIGHT
                '1' -> Vector.DOWN
                '2' -> Vector.LEFT
                '3' -> Vector.UP
                else -> throw IllegalArgumentException()
            }
            Instruction(vector, value.substring(0, 5).toInt(radix = 16))
        }
        println(task2(task2Instructions))
    }

    private fun instructionsToEdgePoints(instructions: List<Instruction>): List<Point> {
        val start = Point(0, 0)
        val points = mutableListOf(start)
        var x = start
        for (instruction in instructions) {
            for (i in 1..instruction.amount) {
                x = x.add(instruction.dir)
                points.add(x)
            }
        }
        return points
    }

    private fun task1(input: List<Instruction>): Int {
        val points = instructionsToEdgePoints(input)
        val box = points.boundingBox()
        val innerPoint =
            points.first { it.y == box.first.y && !points.contains(it.plus(Vector.DOWN)) }.plus(Vector.DOWN)
        val content = floodWithin(innerPoint, points.toSet())
        return content.size + points.toSet().size
    }

    private fun floodWithin(innerPoint: Point, rim: Set<Point>): Set<Point> {
        val result = mutableSetOf(innerPoint)
        val queue = LinkedList<Point>()
        queue.addLast(innerPoint)
        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()
            p.adjacentPoints().filter { !rim.contains(it) && !result.contains(it) }.forEach {
                queue.addLast(it)
                result.add(it)
            }
        }
        return result
    }

    private fun instructionsVertexPoints(instructions: List<Instruction>): List<Point> {
        val start = Point(0, 0)
        val points = mutableListOf(start)
        var x = start
        for (instruction in instructions) {
            x = x.add(instruction.dir * instruction.amount)
            points.add(x)
        }
        return points
    }

    // hint from reddit - use shoelace formula
    private fun task2(input: List<Instruction>): Long {
        val points = instructionsVertexPoints(input)
        var totalVolume = BigInteger.ZERO
        for (i in points.indices) {
            val p1 = points[i]
            val p2 = points[(i + 1) % points.size]
            totalVolume += p1.x.toBigInteger().times(p2.y.toBigInteger()) - p2.x.toBigInteger().times(p1.y.toBigInteger())
        }
        val result = totalVolume.toDouble() / 2
        val edgeVolume = input.sumOf { it.amount } / 2.0 + 1
        return (result + edgeVolume).toLong()
    }

    private data class Instruction(val dir: Vector, val amount: Int)
}