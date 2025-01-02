package cz.libors.aoc.aoc16

import cz.libors.util.*
import kotlin.collections.HashMap

private typealias Floors = List<String>

@Day("Radioisotope Thermoelectric Generators")
object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input11.txt")
            .map { it.substringAfter("contains ").split(Regex("(, |and |\\.)")) }
            .map { list ->
                sort(list.filter { !it.contains("nothing") && it.isNotEmpty() }
                    .map { it.substring(2) }
                    .map { if (it.contains("generator")) it[0].uppercaseChar() else it[0].lowercaseChar() }
                    .joinToString(""))
            }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(floors: Floors) = findPath(floors)
    private fun task2(floors: Floors) = floors
        .mapIndexed { idx, floor -> if (idx == 0) sort(floor + "eEdD") else floor }
        .let { findPath(it) }

    private fun findPath(floors: Floors): Int {
        val itemsCnt = floors.sumOf { it.length }
        val result = bfs(
            start = State(floors, 0),
            endFn = { (f, e) -> f[3].length == itemsCnt && e == 3 },
            neighboursFn = { state ->
                val result = mutableListOf<State>()
                if (state.elevator < 3) result.addAll(neighbours(state, state.elevator + 1))
                if (state.elevator > 0) result.addAll(neighbours(state, state.elevator - 1))
                result
            })
        return result.getScore()!!
    }

    private fun isOk(devices: String): Boolean {
        val generators = devices.count { it.isUpperCase() }
        if (generators == 0 || generators == devices.length) return true
        for (micro in devices.filter { it.isLowerCase() }) {
            if (!devices.contains(micro.uppercaseChar())) return false
        }
        return true
    }

    private fun sort(s: String) = s.toCharArray().sorted().joinToString("")

    private fun neighbours(state: State, toNum: Int): List<State> {
        val from = state.floors[state.elevator]
        val to = state.floors[toNum]
        val result = mutableListOf<State>()
        for (item in from) {
            val newTo = to + item
            if (isOk(newTo)) {
                val newFrom = from.filter { it != item }
                if (isOk(newFrom)) {
                    val newFloors = state.floors.mapIndexed { idx, floor ->
                        when (idx) {
                            state.elevator -> newFrom
                            toNum -> sort(newTo)
                            else -> floor
                        }
                    }
                    result.add(State(newFloors, toNum))
                }
            }
        }
        for (a in 0 until from.length - 1) {
            for (b in a + 1 until from.length) {
                val inElevator = "${from[a]}${from[b]}"
                val newTo = to + inElevator
                val newFrom = from.filter { it != inElevator[0] && it != inElevator[1] }
                if (isOk(inElevator) && isOk(newFrom) && isOk(newTo)) {
                    val newFloors = state.floors.mapIndexed { idx, floor ->
                        when (idx) {
                            state.elevator -> newFrom
                            toNum -> sort(newTo)
                            else -> floor
                        }
                    }
                    result.add(State(newFloors, toNum))
                }
            }
        }
        return result
    }

    // changed hashcode and equals to normalized state to prevent searching same subtrees for different types
    private data class State(val floors: List<String>, val elevator: Int) {
        private val normalized = normalize(this)

        override fun equals(other: Any?) = other is State && other.normalized == normalized && other.elevator == elevator
        override fun hashCode() = normalized.hashCode() + elevator

        private fun normalize(state: State): Pair<List<Int>, Int> {
            val map = HashMap<Char, Int>(10)
            state.floors.forEachIndexed { index, floor ->
                val value = 2L.pow(index).toInt()
                for (ch in floor) {
                    map.merge(ch, value, Int::plus)
                }
            }
            val newMap = mutableMapOf<Char, Int>()
            for ((k, v) in map) {
                val value = if (k.isLowerCase()) v else v shl 8
                newMap.merge(k.lowercaseChar(), value, Int::plus)
            }
            return Pair(newMap.values.sorted(), state.elevator)
        }
    }
}