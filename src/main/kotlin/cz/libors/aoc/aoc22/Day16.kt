package cz.libors.aoc.aoc22

import cz.libors.util.*
private typealias MemoState = Int

@Day(name = "Proboscidea Volcanium")
object Day16 {

    private fun task1(nodes: List<ValveNode>) =
        Solver(nodes.first { it.idx == -1 }, nodes.filter { it.idx != -1 }.toTypedArray(), 30, false).solve()

    private fun task2(nodes: List<ValveNode>) =
        Solver(nodes.first { it.idx == -1 }, nodes.filter { it.idx != -1 }.toTypedArray(), 26, true).solve()

    private data class ValveDef(val name: String, val rate: Int, val outputs: List<String>)
    private class ValveNode(val name: String, val idx: Int, val rate: Int, val targets: Array<Pair<Int, Int>>)

    @JvmStatic
    fun main(args: Array<String>) {
        val valves = readToLines("input16.txt").map {
            val words = it.split(" ")
            var out = it.substringAfter("to valve")
            if (out.startsWith("s")) out = out.substring(1)
            val output = out.split(", ").map { x -> x.trim() }
            ValveDef(words[1], it.findInts()[0], output)
        }
        val nodes = constructValveNodes(valves)
        println(task1(nodes))
        println(task2(nodes))
    }

    private fun constructValveNodes(valves: List<ValveDef>): List<ValveNode> {
        val nameToIdx = valves.mapIndexed { idx, v -> v.name to idx }.toMap()
        val distances = warshall(valves.size) { n -> valves[n].outputs.associate { target -> nameToIdx[target]!! to 1 } }
        val positiveRates = valves.filter { it.rate > 0 }
        val remapIdx = positiveRates.mapIndexed { idx, valve -> valve.name to idx }.toMap()
        val nodes = valves.filter { it.rate > 0 || it.name == "AA" }.map { source ->
            val sourceIdx = nameToIdx[source.name]!!
            val targets = positiveRates.map {
                val targetIdx = nameToIdx[it.name]!!
                remapIdx[it.name]!! to distances[sourceIdx][targetIdx]
            }.filter { it.second != Int.MAX_VALUE && it.second != 0}
            ValveNode(source.name, remapIdx[source.name] ?: -1, source.rate, targets.toTypedArray())
        }
        return nodes
    }

    private class Solver(val start: ValveNode, val nodes: Array<ValveNode>, val totalTime: Int, val withElephant: Boolean) {

        private val memo = HashMap<MemoState, Int>(1500000)

        fun solve() = solve(start, 0, totalTime, withElephant)

        private fun solve(current: ValveNode, open: IntSet, remainingTime: Int, runAlsoElephant: Boolean): Int {
            var memoState: MemoState? = null
            if (!runAlsoElephant) {
                memoState = memoState(current.idx, open, remainingTime)
                val fromCache = memo[memoState]
                if (fromCache != null) return fromCache
            }

            val newOpen = if (current.idx == -1) open else open.set(current.idx)
            val newRemaining = if (current.idx == -1) remainingTime else remainingTime - 1
            val openValveGain = if (current.idx == -1) 0 else current.rate * (newRemaining)

            val elephant = if (runAlsoElephant) solve(start, newOpen, totalTime, false) else 0
            var maxScore = elephant
            for (target in current.targets) {
                if (newOpen.isNotSet(target.first) && target.second < newRemaining - 1) {
                    val targetScore = solve(nodes[target.first], newOpen, newRemaining - target.second, runAlsoElephant)
                    if (targetScore > maxScore) maxScore = targetScore
                }
            }
            val result = maxScore + openValveGain
            if (!runAlsoElephant) memo[memoState!!] = result
            return result
        }

        // Memoization state int instead of data class for faster hash. Tailored to max number of non-zero rate valves.
        private inline fun memoState(valveIdx: Int, open: Int, remainingTime: Int): MemoState =
            open + (valveIdx shl 18) + (remainingTime shl 26)
    }
}