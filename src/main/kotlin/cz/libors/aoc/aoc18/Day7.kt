package cz.libors.aoc.aoc18

import cz.libors.util.Day
import cz.libors.util.add
import cz.libors.util.multiMap
import cz.libors.util.readToLines
import kotlin.math.min

@Day("The Sum of Its Parts")
object Day7 {

    private val regex = Regex("Step (.) must be finished before step (.) can begin.")

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input7.txt")
            .map { line -> regex.matchEntire(line)!!.groupValues.let { Pair(it[1][0], it[2][0]) } }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Pair<Char, Char>>) = process(input, 1) { _ -> 1 }.order
    private fun task2(input: List<Pair<Char, Char>>) = process(input, 5) { x -> 60 + (x - 'A') + 1 }.time

    private fun process(input: List<Pair<Char, Char>>, workersNum: Int, processTimeFn: (Char) -> Int): ProcessResult {
        val orderRules = multiMap<Char, Char>()
        input.forEach { (k, v) -> orderRules.add(v, k) }
        val unprocessed = input.flatMap { listOf(it.first, it.second) }.toMutableSet()
        val expectedNum = unprocessed.size
        val available = sortedSetOf<Char>()
        val processed = mutableListOf<Char>()
        available.addAll(unprocessed.filter { !orderRules.containsKey(it) })
        unprocessed.removeAll(available)
        var time = 0
        val workers = Array(workersNum) { Worker('@', 0) }
        while (processed.size < expectedNum) {
            val newlyAvailable = unprocessed.filter { processed.containsAll(orderRules[it] ?: listOf()) }
            available.addAll(newlyAvailable)
            unprocessed.removeAll(newlyAvailable)
            val freeWorkers = workers.mapIndexedNotNull { idx, w -> if (w.timeLeft == 0) idx else null }
            for (i in 0 until min(freeWorkers.size, available.size)) {
                val toProcess = available.iterator().next()
                workers[freeWorkers[i]].assign(toProcess, processTimeFn(toProcess))
                available.remove(toProcess)
            }
            workers.mapNotNull { it.step() }.forEach { processed.add(it) }
            time++
        }
        return ProcessResult(time, processed.joinToString(""))
    }

    private data class Worker(var assigned: Char, var timeLeft: Int) {
        fun assign(ch: Char, time: Int) {
            assigned = ch
            timeLeft = time
        }

        fun step(): Char? = when (timeLeft) {
            1 -> assigned
            else -> null
        }.also { if (timeLeft > 0) timeLeft-- }
    }

    private data class ProcessResult(val time: Int, val order: String)
}