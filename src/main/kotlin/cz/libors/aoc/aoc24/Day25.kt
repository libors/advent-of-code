package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("Code Chronicle")
object Day25 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input25.txt").splitByEmptyLine()
            .map { it.splitByNewLine().toPointsWithValue().toMap() }
        println(task1(input))
    }

    private fun task1(input: List<Map<Point, Char>>): Int {
        val locks = input.filter { isLock(it) }.map { toPins(it) }
        val keys = input.filter { !isLock(it) }.map { toPins(it) }
        return locks.sumOf { lock -> keys.count { key -> fits(lock, key) } }
    }

    private fun fits(lock: List<Int>, key: List<Int>) = lock.zip(key).all { (l, k) -> l + k <= 5 }
    private fun isLock(x: Map<Point, Char>) = x.count() { it.key.y == 0 && it.value == '#' } == 5
    private fun toPins(x: Map<Point, Char>) = (0..4).map { col -> x.count { it.key.x == col && it.value == '#' } - 1 }
}