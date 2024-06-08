package cz.libors.aoc.aoc23

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines

@Day(name = "Hot Springs")
object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input12.txt").map { line ->
            val split = line.split(" ")
            Config(split[0], split[1].findInts())
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Config>) = input.sumOf { alternatives(it) }
    private fun task2(input: List<Config>) = input.map { c ->
        val x = List(5) { c.x }.joinToString("?")
        val n = mutableListOf<Int>()
        repeat(5) { n.addAll(c.note) }
        Config(x, n.toList())
    }.sumOf { alternatives(it) }

    private fun alternatives(config: Config): Long {
        val cache = mutableMapOf<Config, Long>()
        val result = countAlternatives(config.x, config.note, cache)
        return result
    }

    private fun countAlternatives(x: String, nums: List<Int>, cache: MutableMap<Config, Long>): Long {
        if (x.isEmpty()) return if (nums.isEmpty()) 1 else 0
        if (nums.isEmpty()) return if (x.contains('#')) 0 else 1

        val cacheItem = Config(x, nums)
        val cached = cache[cacheItem]
        if (cached != null) {
            return cached
        }
        var result = 0L
        val char = x[0]
        if (char == '.' || char == '?') {
            result+= countAlternatives(x.substring(1), nums, cache)
        }
        if (char == '#' || char == '?') {
            val n = nums.first()
            if (x.length >= n && !x.substring(0, n).contains('.') && (x.length == n || x[n] != '#')) {
                result += countAlternatives(x.substring(if (x.length == n) n else n + 1), nums.drop(1), cache)
            }
        }
        cache[cacheItem] = result
        return result
    }

    private data class Config(val x: String, val note: List<Int>)
}