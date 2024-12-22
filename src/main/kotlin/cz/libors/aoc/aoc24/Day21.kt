package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("Keypad Conundrum")
object Day21 {

    private val numPadString = """
        789
        456
        123
        #0A
    """.trimIndent()

    private val keyPadString = """
        #^A
        <v>
    """.trimIndent()

    data class KeyPadConfig(
        val numPad: Map<Point, Char>,
        val numPadRev: Map<Char, Point>,
        val keyPad: Map<Point, Char>,
        val keyPadRev: Map<Char, Point>,
        val totalPads: Int
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input21.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>) = processInput(input, createConfig(2))
    private fun task2(input: List<String>) = processInput(input, createConfig(25))

    private fun processInput(input: List<String>, machine: KeyPadConfig): Long {
        data class MemoKey(val ch: Char, val chainId: Int, val prevCh: Char)
        val memo = mutableMapOf<MemoKey, Long>()

        fun process(ch: Char, chainId: Int, prevCh: Char): Long {
            val memoKey = MemoKey(ch, chainId, prevCh)
            val fromMemo = memo[memoKey]
            if (fromMemo != null) return fromMemo

            if (chainId == machine.totalPads - 1) return 1

            val revPad = if (chainId == 0) machine.numPadRev else machine.keyPadRev
            val pad = if (chainId == 0) machine.numPad else machine.keyPad
            val start = revPad[prevCh]!!
            val target = revPad[ch]!!

            val min = getBestPaths(pad, start, target).minOf { path ->
                "A${path}A".zipWithNext { prev, ch -> process(ch, chainId + 1, prev) }.sum()
            }
            memo[memoKey] = min
            return min
        }

        return input.sumOf { code ->
            val buttonPresses = ("A$code").zipWithNext { prev, ch -> process(ch, 0, prev) }.sum()
            getScore(code, buttonPresses)
        }
    }

    private fun createConfig(innerPads: Int): KeyPadConfig {
        val numPad = numPadString.splitByNewLine().toPointsWithValue().filter { it.second != '#' }.toMap()
        val numPadRev = numPad.map { (k, v) -> Pair(v, k) }.toMap()
        val keyPad = keyPadString.splitByNewLine().toPointsWithValue().filter { it.second != '#' }.toMap()
        val keyPadRev = keyPad.map { (k, v) -> Pair(v, k) }.toMap()
        return KeyPadConfig(numPad, numPadRev, keyPad, keyPadRev, innerPads + 2)
    }

    private fun getScore(code: String, buttonPresses: Long) = code.substring(0, code.length - 1).toInt() * buttonPresses

    private fun Vector.toChar() = when (this) {
        Vector.UP -> '^'
        Vector.DOWN -> 'v'
        Vector.LEFT -> '<'
        Vector.RIGHT -> '>'
        else -> throw IllegalArgumentException("Unknown vector: $this")
    }

    private fun getBestPaths(pad: Map<Point, Char>, start: Point, end: Point): List<String> {
        val bfsRes = bfs(start, { it == end }, { it.neighbours().filter { n -> pad.containsKey(n) } })
        val best = bfsRes.getScore()!!
        val paths = dfs(pad, start, end, best)

        val result = mutableListOf<String>()
        for (path in paths) {
            val vectorPath = mutableListOf<Vector>()
            var prev = start
            for (n in path) {
                vectorPath.add(prev.vectorTo(n))
                prev = n
            }
            result.add(vectorPath.map { it.toChar() }.joinToString(""))
        }
        return result
    }

    private fun dfs(pad: Map<Point, Char>, start: Point, end: Point, best: Int): List<List<Point>> {
        val path = mutableListOf<Point>()
        val result = mutableListOf<List<Point>>()

        fun dfsInner(x: Point) {
            if (path.size == best) {
                if (x == end) result.add(path.toList())
                return
            }
            x.neighbours().filter { !path.contains(it) && pad.containsKey(it) }.forEach {
                path.add(it)
                dfsInner(it)
                path.remove(it)
            }
        }
        dfsInner(start)
        return result
    }
}