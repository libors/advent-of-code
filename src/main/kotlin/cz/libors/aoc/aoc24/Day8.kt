package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("Resonant Collinearity")
object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input8.txt").toPointsWithValue()
        val byType = input.filter { it.second != '.' }
            .groupBy { it.second }
            .mapValues { value -> value.value.map { it.first } }
            .values
        val box = input.map { it.first }.boundingBox()

        println(task1(byType, box))
        println(task2(byType, box))
    }

    private fun task1(byType: Collection<List<Point>>, box: Box) = findAllAntinodes(byType, box, false)
    private fun task2(byType: Collection<List<Point>>, box: Box) = findAllAntinodes(byType, box, true)

    private fun findAllAntinodes(byType: Collection<List<Point>>, box: Box, repeat: Boolean): Int {
        val antiSet = mutableSetOf<Point>()
        for (antennas in byType) {
            for (a1 in antennas)
                for (a2 in antennas)
                    if (a1 != a2) antiSet.addAll(findPairAntinodes(a1, a2, repeat, box))
        }
        return antiSet.size
    }

    private fun findPairAntinodes(a1: Point, a2: Point, repeat: Boolean, box: Box): List<Point> {
        val result = mutableListOf<Point>()
        val vector = a1.vectorTo(a2)
        var anti = a2 + vector
        if (repeat) {
            result.add(a1)
            while (box.contains(anti)) {
                result.add(anti)
                anti += vector
            }
        } else {
            if (box.contains(anti)) result.add(anti)
        }
        return result
    }
}