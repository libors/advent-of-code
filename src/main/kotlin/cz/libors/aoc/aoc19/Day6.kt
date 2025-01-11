package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.readToLines

private typealias Pairs = List<Pair<String, String>>

@Day("Universal Orbit Map")
object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input6.txt").map { it.split(")").let { x -> Pair(x[0], x[1]) } }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Pairs): Int {
        val orbitSystems = input.groupBy({ it.first }, { it.second })
        return starters(input).sumOf { countOrbits(it, 1, orbitSystems) }
    }

    private fun task2(input: Pairs): Int {
        val planetCenters = input.associate { Pair(it.second, it.first) }
        val youPath = pathTo("YOU", planetCenters)
        val sanPath = pathTo("SAN", planetCenters)
        val commonPathSize = commonPathSize(youPath, sanPath)
        return youPath.size + sanPath.size - commonPathSize * 2
    }

    private fun starters(input: Pairs): Set<String> {
        val orbiting = input.map { it.second }.toSet()
        return input.map { it.first }.filter { !orbiting.contains(it) }.toSet()
    }

    private fun countOrbits(planet: String, curPathLength: Int, orbitSystems: Map<String, List<String>>): Int {
        val orbiting = orbitSystems[planet] ?: emptyList()
        val dependent = orbiting.map { countOrbits(it, curPathLength + 1, orbitSystems) }.sum()
        return (orbiting.size * curPathLength) + dependent
    }

    private fun pathTo(planet: String, planetCenters: Map<String, String>): List<String> {
        val result = mutableListOf<String>()
        var p: String? = planet
        while (p != null) {
            result.add(p)
            p = planetCenters[p]
        }
        return result.subList(1, result.size).reversed()
    }

    private fun commonPathSize(first: List<String>, second: List<String>) = first.zip(second)
        .takeWhile { it.first == it.second }
        .count()
}