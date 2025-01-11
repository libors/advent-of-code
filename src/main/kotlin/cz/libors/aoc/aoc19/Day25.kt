package cz.libors.aoc.aoc19

import cz.libors.util.*
import cz.libors.util.Vector
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

@Day("Cryostasis")
object Day25 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input25.txt").findLongs().toLongArray()
        TODO("does not work")
        println(task1(input))
    }

    private fun task1(code: LongArray): String {
        val output = MapOutput()
        Computer.create(code, output = output, input = HandInput(output)).runCode()
        return output.bufferState()
    }

    private class History(val map: Map<Point, Long>, val pos: Point)

    fun pathToCheckpointLock() = listOf( // found by hand with debug-on printed map
        "north", "south", "west",
        "take hologram", "south", "north",
        "north", "take space heater", "east",
        "take space law space brochure", "east", "take tambourine",
        "north", "south", "south",
        "north", "west", "west",
        "south", "east", "east",
        "take festive hat", "east", "take food ration",
        "east", "take spool of cat6", "west",
        "north", "south", "west",
        "south", "west", "east",
        "east", "east", "take fuel cell",
        "east"
    )

    private class MapOutput : ArrayOutput() {
        private val buffer = StringBuilder()
        private var current = Point(0, 0)
        private var map = java.util.HashMap<Point, Long>()
        private val mapPrinter = PlanePrinter(mapOf(0L to ' '), 0, unknown = { it.toInt().toChar() })
        private val history = LinkedList<History>()

        init {
            map[Point(0, 0)] = '.'.code.toLong()
        }

        override fun write(value: Long) {
            buffer.append(value.toInt().toChar())
        }

        fun returnMove() {
            map = HashMap(history.last.map)
            current = history.last.pos
            history.removeLast()
        }

        fun bufferState() = buffer.toString()

        fun move(vector: Vector) {
            history.add(History(HashMap(map), current))
            val point = current + vector
            if ((map[point] ?: 0L) != 0L) {
                current += vector
                map[current] = '.'.code.toLong()
            }
        }

        fun printMap() {
            debug(mapPrinter.print(map, current))
        }

        fun processOutput(): String {
            val sb = StringBuilder()
            updateMap(buffer.split("\n"))
            sb.append(buffer)
            buffer.clear()
            return sb.toString()
        }

        enum class State { ITEMS, DIRECTIONS, UNKNOWN }

        private fun updateMap(lines: List<String>) {
            var state = State.UNKNOWN
            for (line in lines) {
                when (state) {
                    State.UNKNOWN -> {
                        if (line == "Items here:") state = State.ITEMS
                        else if (line == "Doors here lead:") state = State.DIRECTIONS
                    }
                    State.ITEMS -> {
                        if (line.isBlank()) state = State.UNKNOWN else {

                        }
                    }
                    State.DIRECTIONS -> {
                        if (line.isBlank()) state = State.UNKNOWN else {
                            val direction = line.substringAfterLast(" ")
                            val vector = Vector.from(direction) ?: throw RuntimeException()
                            val point = current.add(vector)
                            if (map[point] ?: 0L == 0L) map[point] = '+'.toLong()
                        }
                    }

                }
            }
        }
    }

    private class LockOpener(val itemList: List<String>, val direction: String) {
        val combinations = ArrayList<IntArray>()
        var currentCombination = 0

        init {
            for (i in 1..itemList.size)
                combinations(i, itemList.size).forEach { combinations.add(it) }
        }

        fun tryNextCombination(curItems: Set<String>): List<String> {
            val result = ArrayList<String>()
            val comb = combinations[currentCombination++]
            curItems.forEach { result.add("drop $it") }
            comb.forEach { result.add("take ${itemList[it]}") }
            result.add(direction)
            debug("trying combination $currentCombination / ${combinations.size}: $result")
            return result
        }

    }

    private class HandInput(val output: MapOutput) : QueueInput(ArrayBlockingQueue(1000)) {
        private val keyboard = Scanner(System.`in`)
        private val automaticActions: Queue<String> = LinkedList()
        private val items = HashSet<String>()
        private var lockOpener: LockOpener? = null

        override fun read(): Long {
            if (queue.isEmpty()) {
                val outputString = output.processOutput()
                debug(outputString)
                output.printMap()
                var s: String
                if (automaticActions.isNotEmpty())
                    s = automaticActions.poll()
                else if (lockOpener != null) {
                    automaticActions.addAll(lockOpener!!.tryNextCombination(items))
                    s = automaticActions.poll()
                } else {
                    s = enterAction()
                    if (customAction(s)) {
                        if (automaticActions.isNotEmpty()) s = automaticActions.poll() else s = enterAction()
                    }
                }
                debug("running action $s")
                handleTakeDrop(s)
                Vector.from(s)?.let { output.move(it) }
                enqueue(s)
            }
            return super.read()
        }

        private fun enterAction(): String {
            print("Enter command (check/open): ")
            return keyboard.nextLine()
        }

        private fun enqueue(s: String) {
            s.forEach { queue.put(it.toLong()) }
            queue.put(10L)
        }

        private fun handleTakeDrop(s: String) {
            if (s.startsWith("take")) {
                items.add(s.substringAfter(" "))
            } else if (s.startsWith("drop")) {
                items.remove(s.substringAfter(" "))
            }
        }

        private fun customAction(action: String): Boolean {
            if (action == "x") {
                output.returnMove()
                output.printMap()
                return true
            } else if (action == "check") {
                pathToCheckpointLock().forEach { automaticActions.add(it) }
                return true
            } else if (action == "open") {
                lockOpener = LockOpener(ArrayList(items), "south")
                automaticActions.addAll(lockOpener!!.tryNextCombination(items))
                return true
            } else {
                return false
            }
        }
    }
}