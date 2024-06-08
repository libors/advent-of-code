package cz.libors.aoc.aoc23

import cz.libors.util.*

@Day("If You Give A Seed A Fertilizer")
object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input5.txt").splitByEmptyLine()
        val seeds = input.first().findLongs()
        val maps = input.subList(1, input.size).map { map ->
            val lines = map.splitByNewLine()
            lines.subList(1, lines.size).map { it.findLongs() }
        }.map { Mapping(it) }
        println(task1(seeds, maps))
        println(task2(seeds, maps))
    }

    private fun task2(seeds: List<Long>, maps: List<Mapping>): Long {
        val splitPoints = getSplitPoints(maps)
        return (seeds.indices step 2).map { idx ->
            val start = seeds[idx]
            val end = start + seeds[idx + 1]
            val interval = LongRange(start, end)
            val seedsToCheck = splitPoints.filter { interval.contains(it) }
            seedsToCheck.map { convert(it, maps) }.minOf { it }
        }.minOf { it }
    }

    private fun task1(seeds: List<Long>, maps: List<Mapping>) = seeds.map { convert(it, maps) }.minOf { it }

    private fun convert(input: Long, maps: List<Mapping>) = maps.fold(input) { acc, map -> map.getOutput(acc) }

    private class Mapping(val intervals: List<List<Long>>) {
        fun getOutput(input: Long): Long {
            for (i in intervals) {
                val destStart = i[0]
                val srcStart = i[1]
                val size = i[2]
                if (input in (srcStart until srcStart + size)) {
                    return destStart + input - srcStart
                }
            }
            return input
        }
    }

    private fun getSplitPoints(mappings: List<Mapping>) : List<Long> {
        var pointsSet = mappingBounds(mappings.last()).toSet()
        for (mapping in mappings.subList(0, mappings.size - 1).reversed()) {
            val fromLower = pointsSet.map { getSourceForTarget(it, mapping) }
            val fromMapping = mappingBounds(mapping)
            pointsSet = (fromLower + fromMapping).toSet()
        }
        return pointsSet.toList().sorted()
    }

    private fun getSourceForTarget(target: Long, mapping: Mapping): Long {
        val targetIntervals = mapping.intervals.map { LongRange(it[0], it[0] + it[2]) to it }
        for (i in targetIntervals) {
            if (i.first.contains(target)) {
                val targetStart = i.second[0]
                val sourceStart = i.second[1]
                val diff = target - targetStart
                return sourceStart + diff
            }
        }
        return target
    }

    private fun mappingBounds(mapping: Mapping) = mapping.intervals.map { it[1] } + mapping.intervals.map { it[1] + it[2] }

}