package cz.libors.aoc.aoc22

import cz.libors.util.findInts
import cz.libors.util.readToLines
import java.util.*
import kotlin.collections.HashMap

typealias OpenSet = Int

object Day16 {

    private var everythingOpen: OpenSet = 0
    private var precomputedRates: IntArray = IntArray(0)

    private var sizeStopped = 0L
    private var stopped = 0L
    private var positions = 0L
    private val hash = mutableMapOf<PathRec, Pair<Int, Int>>()
    private val hash2 = HashMap<PathRecKey2, Pair<Int, Int>>(10000000)

    private fun task1(valves: List<Valve>): Int {
        positions = 0
        hash.clear()
        val start = valves.find { it.name == "AA" }!!
        return solve(0, 0, start, 30, listOf())
    }

    private fun task2(valves: List<Valve>): Int {
        positions = 0
        hash2.clear()
        return solve2(valves)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val valveTuples = readToLines("input16.txt").map {
            val words = it.split(" ")
            var out = it.substringAfter("to valve")
            if (out.startsWith("s")) out = out.substring(1)
            val output = out.split(", ").map { x -> x.trim() }
            Triple(words[1], it.findInts()[0], output)
        }
        val tuplesWithAddintionalInfo = valveTuples
            .sortedWith { x, y -> y.second - x.second }
            .mapIndexed { idx, it -> it to Pair(mutableListOf<Valve>(), idx) }

        val valves = tuplesWithAddintionalInfo.map {
            val rate = it.first.second
            val opener = if (rate == 0) 0 else 1 shl it.second.second
            Valve(opener, it.first.first, it.first.second, it.second.first)
        }
        val valveByName = valves.associateBy { it.name }

        tuplesWithAddintionalInfo.forEach {
            it.first.third.forEach { vName -> it.second.first.add(valveByName[vName]!!) }
        }

        val nonZeroRateValves = valves.filter { it.rate > 0 }
        everythingOpen = nonZeroRateValves.fold(0) { x, v -> v.open(x) }
        precomputedRates = IntArray(everythingOpen + 1)
        (0..everythingOpen).forEachIndexed { idx, it ->
            val v = nonZeroRateValves.fold(0) { x, v -> if (v.canOpen(it)) x + v.rate else x }
            precomputedRates[idx] = v
        }

        println("*" + task1(valves))
        println("**" + task2(valves))
        println(positions)
    }

    private fun solve2(valves: List<Valve>): Int {
        val queue: Queue<QueRec> = PriorityQueue { a, b -> (b.score - a.score) }
        val startValve = valves.find { it.name == "AA" }!!
        queue.add(QueRec(Pos(startValve, startValve), 0, 26, 0))
        var maxScore = 0
        val start = System.currentTimeMillis()
        while (!queue.isEmpty()) {
            val x = queue.poll()
            if (++positions % 10000000 == 0L) {
                println()
                println("positions: ${positions / 1000000} mil")
                println("hash stopped: ${stopped / 1000000} mil, score stopped: ${sizeStopped / 1000000} mil")
                println("queue size: ${queue.size}, hash size: ${hash2.size}")
                println()
            }

            if (x.open == everythingOpen || x.remainingTime == 0) {
                if (x.score > maxScore) {
                    maxScore = x.score
                    println("*** new max: $maxScore in ${(System.currentTimeMillis() - start) / 1000} sec")
                }
                continue
            }

            val myValve = x.cur.me
            val elValve = x.cur.el
            val iCanOpen = x.cur.me.canOpen(x.open)
            val elCanOpen = x.cur.el.canOpen(x.open)

            if (x.cur.el != x.cur.me && iCanOpen && elCanOpen) {
                val newScore = x.score + (myValve.rate * x.timeDown()) + (elValve.rate * x.timeDown())
                val rec = QueRec(x.cur, x.cur.me.open(x.cur.el.open(x.open)), x.timeDown(), newScore)
                if (checkCanThrowOut(rec, maxScore)) queue.add(rec)
            }
            val myOutputs = myValve.leadsTo
            val elOutputs = elValve.leadsTo
            for (myOutput in myOutputs) {
                for (elOutput in elOutputs) {
                    val rec = QueRec(Pos(myOutput, elOutput), x.open, x.timeDown(), x.score)
                    if (checkCanThrowOut(rec, maxScore)) queue.add(rec)
                }
            }
            if (iCanOpen) {
                val newScore = x.score + (myValve.rate * x.timeDown())
                for (elOutput in elOutputs) {
                    val rec = QueRec(Pos(x.cur.me, elOutput), x.cur.me.open(x.open), x.timeDown(), newScore)
                    if (checkCanThrowOut(rec, maxScore)) queue.add(rec)
                }
            }
            if (elCanOpen) {
                val newScore = x.score + (elValve.rate * (x.remainingTime - 1))
                for (myOutput in myOutputs) {
                    val rec = QueRec(Pos(myOutput, x.cur.el), x.cur.el.open(x.open), x.timeDown(), newScore)
                    if (checkCanThrowOut(rec, maxScore)) queue.add(rec)
                }

            }

        }
        println("Running time(s): ${(System.currentTimeMillis() - start) / 1000}")
        return maxScore
    }

    private inline fun checkCanThrowOut(rec: QueRec, maxScore: Int): Boolean {
        if (precomputedRates[rec.open] * rec.timeDown() + rec.score < maxScore) {
            sizeStopped++
            return false
        }
        val hashKey = PathRecKey2(rec.cur.me.name, rec.cur.el.name, rec.open)
        val hashScore = hash2[hashKey]
        if (hashScore != null && (hashScore.first >= rec.score && hashScore.second >= rec.remainingTime)) {
            stopped++
            return false
        } else {
            hash2[hashKey] = Pair(rec.score, rec.remainingTime)
        }

        return true
    }

    private fun solve(score: Int, open: OpenSet, current: Valve, remainingTime: Int, path: List<PathRec>): Int {
        if (++positions % 10000000 == 0L) println(positions)
        if (open == everythingOpen || remainingTime == 0) {
            return score
        }
        val hashKey = PathRec(current.name, open)
        val hashScore = hash[hashKey]
        if (hashScore != null && (hashScore.first >= score && hashScore.second >= remainingTime)) {
            return 0
        } else {
            hash[hashKey] = Pair(score, remainingTime)
        }

        val canOpen = current.canOpen(open)
        val subScores = mutableListOf<Int>()
        if (canOpen) {
            val newScore = score + (current.rate * (remainingTime - 1))
            subScores.add(solve(newScore, current.open(open), current, remainingTime - 1, path))
        }
        val outputs = current.leadsTo
        for (output in outputs) {
            subScores.add(solve(score, open, output, remainingTime - 1, path + PathRec(output.name, open)))
        }
        return subScores.maxOf { it }
    }

    data class QueRec(val cur: Pos, val open: OpenSet, val remainingTime: Int, val score: Int) {
        fun timeDown() = remainingTime - 1
    }

    data class PathRec(val cur: String, val open: OpenSet)

    data class Pos(val me: Valve, val el: Valve)
    data class PathRecKey2(val me: String, val el: String, val open: OpenSet)

    data class Valve(
        val opener: Int,
        val name: String,
        val rate: Int,
        val leadsTo: List<Valve>
    ) {
        inline fun open(set: OpenSet): OpenSet = set or opener
        inline fun canOpen(set: OpenSet): Boolean = rate > 0 && (set and opener) == 0
        override fun toString(): String {
            return "name: " + name + " rate: " + rate + " leadsTo: " + leadsTo.map { v -> v.name }
        }
    }

}