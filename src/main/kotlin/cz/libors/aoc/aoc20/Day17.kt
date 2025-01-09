package cz.libors.aoc.aoc20

import cz.libors.util.*

@Day("Conway Cubes")
object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input17.txt").toPointsWithValue()
            .filter { it.second == '#' }
            .map { Point(it.first.x, it.first.y) }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Point>) = run(input, 3)
    private fun task2(input: List<Point>) = run(input, 4)

    private fun run(input: List<Point>, dims: Int): Int {
        var x = translate(input, dims)
        repeat(6) { x = derive(x, dims) }
        return x.size
    }

    private fun translate(input: List<Point>, dims: Int): Set<List<Int>> {
        val result = mutableSetOf<List<Int>>()
        for (p in input) {
            val l = mutableListOf(p.x, p.y)
            repeat(dims - 2) { l.add(0) }
            result.add(l)
        }
        return result
    }

    private fun derive(s: Set<List<Int>>, dims: Int): Set<List<Int>> {
        val result = mutableSetOf<List<Int>>()
        val mins = (0 until dims).map { n -> s.minOf { it[n] } }
        val maxs = (0 until dims).map { n -> s.maxOf { it[n] } }
        val array = IntArray(dims)
        fun iterate(n: Int) {
            if (n == -1) {
                val p = array.toList()
                val activeNeighbours = neighbors(p).count { s.contains(it) }
                if (s.contains(p) && activeNeighbours in 2..3) result.add(p)
                if (!s.contains(p) && activeNeighbours == 3) result.add(p)
            } else {
                val min = mins[n]
                val max = maxs[n]
                for (i in min - 1 .. max + 1) {
                    array[n] = i
                    iterate(n - 1)
                }
            }
        }

        iterate(dims - 1)
        return result
    }

    private fun neighbors(p: List<Int>): Set<List<Int>> {
        val result = mutableSetOf<List<Int>>()
        val array = IntArray(p.size)

        fun generate(n: Int) {
            if (n == -1) {
                result.add(array.toList())
            } else {
                for (i in -1..1) {
                    array[n] = p[n] + i
                    generate(n - 1)
                }
            }
        }

        generate(p.size - 1)
        result.remove(p)
        return result
    }
}