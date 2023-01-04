package cz.libors.aoc.aoc22

import cz.libors.util.debug
import cz.libors.util.findInts
import cz.libors.util.readToLines

object Day19 {

    private val OR = 0
    private val GE = 3
    private var totmax = 0
    private val hash: MutableMap<HashKey, Int> = mutableMapOf()

    private fun task1(bluePrints: List<List<List<Int>>>) =
        bluePrints.mapIndexed { idx, it ->
            totmax = 0
            hash.clear()
            dfs(it, listOf(1, 0, 0, 0), listOf(0, 0, 0, 0), 24) * (idx + 1)
        }.sumOf { it }

    private fun task2(bluePrints: List<List<List<Int>>>) =
        bluePrints.take(3).map {
            totmax = 0
            hash.clear()
            dfs(it, listOf(1, 0, 0, 0), listOf(0, 0, 0, 0), 32)
        }.reduce { x, y -> x * y }

    @JvmStatic
    fun main(args: Array<String>) {
        val bluePrints = readToLines("input19.txt")
            .map { it.substring("Blueprint 1:  ".length).findInts() }
            .map {
                listOf(
                    listOf(it[0], 0, 0, 0), // ore
                    listOf(it[1], 0, 0, 0),  // clay
                    listOf(it[2], it[3], 0, 0), // obsidian
                    listOf(it[4], 0, it[5], 0) // geode
                )
            }
        println(task1(bluePrints))
        println(task2(bluePrints))
    }

    var calls = 0
    fun dfs(
        costs: List<List<Int>>,
        robots: List<Int>,
        resources: List<Int>,
        time: Int
    ): Int {
        if (++calls % 1000000 == 0) {
            debug(calls)
        }
        if (time == 0) return resources[GE]
        if (totmax > 0 && resources[GE] + possibleMax(time, robots[GE]) <= totmax) {
            return 0
        }
        val hashKey = buildKey(robots.toList(), resources.toList(), time)
        val hashResult = hash[hashKey]
        if (hashResult != null) return hashResult

        val newResources = robots.mapIndexed { idx, x -> resources[idx] + x }
        val scores = mutableListOf<Int>()
        (GE downTo OR).forEach {
            if (resources.coverCosts(costs[it]))
                scores.add(dfs(costs, robots.increase(it), newResources.afterBuy(costs[it]), time - 1))
        }
        scores.add(dfs(costs, robots, newResources, time - 1))
        val max = scores.maxOf { it }
        hash[hashKey] = max
        if (max > totmax) totmax = max
        return max
    }

    private inline fun buildKey(robots: List<Int>, resources: List<Int>, time: Int): HashKey {
        val i = robots[0] + (robots[1] shl 8) + (robots[2] shl 16) + (robots[3] shl 24)
        val j: Long = resources[0] +
                (resources[1].toLong() shl 10) +
                (resources[2].toLong() shl 20) +
                (resources[3].toLong() shl 30) +
                time shl 40
        return HashKey(i, j)
    }

    private inline fun possibleMax(time: Int, gRobots: Int) = gRobots * time + (time * time - 1) / 2

    private inline fun List<Int>.coverCosts(costs: List<Int>) = this.zip(costs).all { it.first >= it.second }
    private inline fun List<Int>.increase(index: Int) =
        mapIndexed { idx, x -> if (idx == index) x + 1 else x }

    private inline fun List<Int>.afterBuy(costs: List<Int>) = this.zip(costs).map { it.first - it.second }

    data class HashKey(val robots: Int, val resourcesAndTime: Long)
}