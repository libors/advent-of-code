package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.Point
import cz.libors.util.findLongs
import cz.libors.util.readToText
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

@Day("Set and Forget")
object Day17 {

    private const val SCAFFOLD = 35L
    private const val NEWLINE = 10L

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input17.txt").findLongs()
        println(task1(input.toLongArray()))
        println(task2(input.toLongArray()))
    }

    private fun task1(code: LongArray): Int {
        val output = Computer.create(code).runCode().getOutput()
        val points = toPoints(output)
        return intersections(points).sumOf { it.x * it.y }
    }

    private fun task2(code: LongArray): Long {
        val a = "R,10,L,10,L,12,R,6"
        val b = "L,10,R,12,R,12"
        val c = "R,6,R,10,L,10"
        val main = "B,C,B,A,C,A,C,A,B,A"
        code[0] = 2L
        val inputQueue = createInstructions(listOf(main, a, b, c))
        return Computer.create(code, Input.queue(inputQueue)).runCode().getLastOutput()
    }

    private fun createInstructions(inputs: List<String>): BlockingQueue<Long> {
        val queue = ArrayBlockingQueue<Long>(1000)
        for (input in inputs) {
            input.toCharArray().forEach{  queue.add(it.toLong()) }
            queue.add(NEWLINE)
        }
        queue.add('n'.code.toLong())
        queue.add(NEWLINE)
        return queue
    }

    private fun intersections(points: Map<Point, Long>) = points.entries.filter {
        it.value == SCAFFOLD &&
                it.key.neighbours().count { p -> points[p] == SCAFFOLD } == 4
    }.map { it.key }

    private fun toPoints(output: List<Long>): Map<Point, Long> {
        val result = mutableMapOf<Point, Long>()
        var x = 0
        var y = 0
        for (value in output) {
            if (value == NEWLINE) {
                y += 1
                x = 0
            } else
                result[Point(x++, y)] = value
        }
        return result
    }
}