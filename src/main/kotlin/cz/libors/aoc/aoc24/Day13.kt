package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("Claw Contraption")
object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input13.txt").splitByEmptyLine()
            .map { block ->
                val lines = block.splitByNewLine().map { it.findLongs() }
                Machine(lines[0][0], lines[0][1], lines[1][0], lines[1][1], lines[2][0], lines[2][1])
            }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Machine>) = input.sumOf { computePresses(it) }
    private fun task2(input: List<Machine>) = 10000000000000.let { addNum ->
        input.map { Machine(it.ax, it.ay, it.bx, it.by, it.px + addNum, it.py + addNum) }
    }.sumOf { computePresses(it) }

    private fun computePresses(input: Machine): Long = with(input) {
        // computed on paper from Aax+Bbx=px, Aay+Bby=py
        val b = 1.0 * (py * ax - px * ay) / (by * ax - bx * ay)
        val a = 1.0 * (px - b * bx) / ax
        return if (b.isLong() && a.isLong()) 3 * a.toLong() + b.toLong() else 0
    }

    private data class Machine(val ax: Long, val ay: Long, val bx: Long, val by: Long, val px: Long, val py: Long)
}