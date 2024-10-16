package cz.libors.util

import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Day(val name: String)

typealias IntSet = Int

fun IntSet.set(i: Int): IntSet = this or (1 shl i)
fun IntSet.isSet(i: Int): Boolean = (this and (1 shl i)) != 0
fun IntSet.isNotSet(i: Int): Boolean = (this and (1 shl i)) == 0

private fun resolveResourcePath(x: String): String {
    val traceLine = Thread.currentThread().stackTrace.first { it.className.contains("aoc.aoc") }
    val year = traceLine.className.findPositiveInts()[0]
    return "$year/$x"
}

fun readToLines(name: String): List<String> =
    ClassLoader.getSystemResource(resolveResourcePath(name)).openStream().use {
        return it.reader().readLines()
    }

fun readToText(name: String): String =
    ClassLoader.getSystemResource(resolveResourcePath(name)).openStream().use {
        return it.reader().readText()
    }

fun List<String>.toPointsWithValue() = this.flatMapIndexed { lineIdx, line ->
    line.mapIndexed { rowIdx, row -> Point(rowIdx, lineIdx) to row }
}

fun String.findInts(): List<Int> = Regex("-?[0-9]+")
    .findAll(this)
    .map { it.value.toInt() }
    .toList()

fun String.findLongs(): List<Long> = Regex("-?[0-9]+")
    .findAll(this)
    .map { it.value.toLong() }
    .toList()

fun String.findAlphanums(): List<String> = Regex("[0-9a-zA-Z]+")
    .findAll(this)
    .map { it.value }
    .toList()

fun String.findPositiveInts(): List<Int> = Regex("[0-9]+")
    .findAll(this)
    .map { it.value.toInt() }
    .toList()

fun String.take(start: Int, every: Int): List<Char> {
    val result = mutableListOf<Char>()
    for (i in start until this.length step every) {
        result.add(this[i])
    }
    return result
}

fun String.splitByEmptyLine(): List<String> = this.split(Regex("\r?\n\r?\n"))
fun String.splitByNewLine(): List<String> = this.split(Regex("\r?\n"))

fun Int.posMod(x: Int) = (this % x).let { if (it >= 0) it else it + x }

fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

fun <T> Iterable<T>.toTuples(n: Int): List<List<T>> {
    val iterator = this.iterator()
    val result = mutableListOf<List<T>>()
    while (iterator.hasNext()) {
        val subList = mutableListOf<T>()
        for (i in 1..n)
            subList.add(iterator.next())
        result.add(subList)
    }
    return result
}

data class Point(val x: Int, val y: Int) {
    fun manhattanDistance(oth: Point) = vectorTo(oth).manhattanDistance()

    operator fun plus(other: Vector) = this.add(other)
    operator fun minus(other: Vector) = this.add(other.negative())

    fun right() = this + Vector.RIGHT
    fun left() = this + Vector.LEFT
    fun up() = this + Vector.UP
    fun down() = this + Vector.DOWN

    fun add(other: Vector) = Point(x + other.x, y + other.y)
    fun vectorTo(other: Point) = Vector(other.x - x, other.y - y)
    fun switch() = Point(y, x)
    fun adjacentPoints(): List<Point> = listOf(add(Vector.UP), add(Vector.DOWN), add(Vector.LEFT), add(Vector.RIGHT))
    fun diagonalPoints(): List<Point> =
        listOf(add(Vector(1, 1)), add(Vector(1, -1)), add(Vector(-1, 1)), add(Vector(-1, -1)))

    fun touchingPoints(): List<Point> = adjacentPoints() + diagonalPoints();

    fun series(direction: Vector, points: Set<Point>): List<Point> {
        val result = mutableListOf(this)
        var p = this.plus(direction)
        while (points.contains(p)) {
            result.add(p)
            p = p.plus(direction)
        }
        return result;
    }

    override fun toString() = "[$x, $y]"

}

typealias Box = Pair<Point, Point>

fun Iterable<Point>.boundingBox() = Box(
    Point(minOf { it.x }, minOf { it.y }),
    Point(maxOf { it.x }, maxOf { it.y })
)

fun Box.contains(p: Point) =
    this.first.x <= p.x && this.first.y <= p.y && this.second.x >= p.x && this.second.y >= p.y

fun Box.center() = Point((this.first.x + this.second.x) / 2, (this.first.y + this.second.y) / 2)
fun Box.size() = Pair(this.second.x - this.first.x + 1, this.second.y - this.first.y + 1)

fun Pair<Point, Point>.invert(points: Set<Point>): Set<Point> {
    val result = mutableSetOf<Point>()
    for (x in this.first.x..this.second.x)
        for (y in this.first.y..this.second.y) {
            val p = Point(x, y)
            if (!points.contains(p)) result.add(p)
        }
    return result.toSet()
}

fun <T> String.readTree(fn: (String) -> T): TreeNode<T> {
    val open = "[<({"
    val close = "]>)}"
    val stack = LinkedList<MutableList<TreeNode<T>>>()
    var value = ""
    var bracket = ' '
    var current = mutableListOf<TreeNode<T>>()
    for (i in this) {
        when (i) {
            in open -> {
                stack.push(current)
                bracket = i
                current = mutableListOf()
            }
            in close -> {
                if (value != "") {
                    current.add(TreeNode(null, fn(value), listOf()))
                    value = ""
                }
                val pushed = stack.pop()
                pushed.add(TreeNode(bracket, null, current))
                current = pushed
            }
            ',' -> {
                if (value != "") {
                    current.add(TreeNode(null, fn(value), listOf()))
                    value = ""
                }
            }
            else -> value += i
        }
    }
    return current.get(0)
}

data class TreeNode<T>(val bracket: Char? = '[', val v: T? = null, val items: List<TreeNode<T>> = listOf()) {
    fun isValue() = v!= null
    override fun toString(): String {
        return if (v == null) "[${items.joinToString(",")}]" else v?.toString()
    }
}

data class Body(val points: Set<Point>) {
    fun highest() = points.maxOf { it.y }
    fun lowest() = points.minOf { it.y }
    fun rightest() = points.maxOf { it.x }
    fun leftest() = points.minOf { it.x }
    fun move(v: Vector) = Body(points.map { it + v }.toSet())
    fun left(n: Int = 1) = move(Vector.LEFT * n)
    fun right(n: Int = 1) = move(Vector.RIGHT * n)
    fun down(n: Int = 1) = move(Vector.DOWN * n)
    fun up(n: Int = 1) = move(Vector.UP * n)
    fun collides(other: Body) = points.intersect(other.points).isNotEmpty()
    operator fun plus(other: Body) = Body(points + other.points)

}

fun List<Int>.toPoint() = Point(this[0], this[1])

data class Vector(val x: Int, val y: Int) {
    companion object {
        val LEFT = Vector(-1, 0)
        val RIGHT = Vector(1, 0)
        val UP = Vector(0, -1)
        val DOWN = Vector(0, 1)
        val LEFT_UP = Vector(-1, -1)
        val RIGHT_UP = Vector(1, -1)
        val LEFT_DOWN = Vector(-1, 1)
        val RIGHT_DOWN = Vector(1, 1)

        fun from(s: String): Vector? = when (s.lowercase()) {
            "north", "up", "u", "^" -> UP
            "east", "right", "r", ">" -> RIGHT
            "south", "down", "d", "v" -> DOWN
            "west", "left", "l", "<" -> LEFT
            else -> null
        }

    }

    fun normalize(): Vector = gcd(x, y).absoluteValue
        .let { if (it == 0) this else Vector(x / it, y / it) }

    fun turnLeft() = Vector(y, -x)
    fun turnRight() = Vector(-y, x)
    fun negative() = Vector(-x, -y)
    operator fun times(factor: Int) = Vector(x * factor, y * factor)
    fun manhattanDistance() = abs(x) + abs(y)
}

data class Vector3(val x: Int, val y: Int, val z: Int) {
    fun normalize(): Vector3 = gcd(z, gcd(x, y)).absoluteValue
        .let { if (it == 0) this else Vector3(x / it, y / it, z / it) }
}

data class Point3(val x: Int, val y: Int, val z: Int) {

    companion object {

    }

    fun adjacent() = listOf(
        Point3(x, y, z + 1),
        Point3(x, y, z - 1),
        Point3(x, y + 1, z),
        Point3(x, y - 1, z),
        Point3(x + 1, y, z),
        Point3(x - 1, y, z)
    )

    operator fun plus(v: Vector3) = Point3(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector3) = Point3(x - v.x, y - v.y, z - v.z)

    fun toVector() = Vector3(x, y, z)

    fun inRect(min: Point3, max: Point3) =
        x in min.x..max.x && y in min.y..max.y && z in min.z..max.z
}

fun Iterable<Point3>.boundingBox3() = Pair(
    Point3(minOf { it.x }, minOf { it.y }, minOf { it.z }),
    Point3(maxOf { it.x }, maxOf { it.y }, maxOf { it.z })
)

fun <K> flood(start: K, neighborsFn: (K) -> Iterable<K>): Set<K> {
    val result = mutableSetOf(start)
    val queue = LinkedList<K>()
    queue.add(start)
    while (queue.isNotEmpty()) {
        neighborsFn(queue.removeLast())
            .filter { !result.contains(it) }
            .forEach {
                result.add(it)
                queue.addLast(it)
            }
    }
    return result
}

data class Interval(val from: Long, val to: Long) {
    fun isSubOf(other: Interval) = from >= other.from && to <= other.to
    fun contains(other: Interval) = other.isSubOf(this)
    fun overlaps(other: Interval) = from <= other.to && other.from <= to
    fun isEmpty() = EMPTY == this
    fun union(other: Interval) = if (this.overlaps(other))
        Interval(min(from, other.from), max(to, other.to)) else EMPTY

    fun intersect(other: Interval) = if (this.overlaps(other))
        Interval(max(from, other.from), min(to, other.to)) else EMPTY

    companion object {
        val EMPTY: Interval = Interval(1, 0)
        fun from(x: Long, y: Long) = if (x <= y) Interval(x, y) else Interval(y, x)
    }
}

typealias BinOperation = (Long, Long) -> Long

fun BinOperation(ch: Char): BinOperation = when (ch) {
    '+' -> { a, b -> a + b }
    '-' -> { a, b -> a - b }
    '*' -> { a, b -> a * b }
    '/' -> { a, b -> a / b }
    '=' -> { a, b -> a.compareTo(b).toLong() }
    else -> throw IllegalArgumentException("Unknown op : $ch")
}

val DEBUG_ON = false

fun debug() {
    if (DEBUG_ON) println()
}

fun debug(x: Any) {
    if (DEBUG_ON) println(x)
}


fun warshall(nodes: Int, distFn: (Int) -> Map<Int, Int>): Array<IntArray> {
    val matrix = Array(nodes) { IntArray(nodes) { Int.MAX_VALUE } }
    for (i in 0 until nodes) {
        matrix[i][i] = 0
    }
    for (n in 0 until nodes) {
        for ((target, distToTarget) in distFn(n)) {
            matrix[n][target] = distToTarget
        }
    }
    for (thru in 0 until nodes)
        for (source in 0 until nodes)
            for (dest in 0 until nodes) {
                val toThru = matrix[source][thru]
                val fromThru = matrix[thru][dest]
                val toDest = matrix[source][dest]
                if (toThru != Int.MAX_VALUE && fromThru != Int.MAX_VALUE && toThru + fromThru < toDest)
                    matrix[source][dest] = toThru + fromThru
            }
    return matrix
}

fun <K> dijkstra(
    start: K,
    endFn: (K) -> Boolean,
    distanceFn: (K, K) -> Int = { _, _ -> 1 },
    neighboursFn: (K) -> Iterable<K>
): ShortestPath<K> {
    val queue = PriorityQueue(compareBy<ScoredNode<K>> { it.score })
    val seen = mutableMapOf<K, PathToNode<K>>()
    seen[start] = PathToNode(start, 0)
    queue.add(ScoredNode(start, 0))
    var endNode: K? = null
    while (queue.isNotEmpty() && endNode == null) {
        val (node, score) = queue.poll()
        if (endFn(node)) endNode = node
        for (neighbour in neighboursFn(node)) {
            if (!seen.containsKey(neighbour)) {
                val scoredNode = ScoredNode(neighbour, distanceFn(node, neighbour) + score)
                queue.add(scoredNode)
                seen[scoredNode.node] = PathToNode(node, scoredNode.score)
            }
        }
    }
    return ShortestPath(start, seen, endNode)
}

fun <K> bfs(
    start: K,
    endFn: (K) -> Boolean,
    neighboursFn: (K) -> Iterable<K>
): ShortestPath<K> {
    val queue = LinkedList<ScoredNode<K>>()
    val seen = mutableMapOf<K, PathToNode<K>>()
    seen[start] = PathToNode(start, 0)
    queue.add(ScoredNode(start, 0))
    var endNode: K? = null
    while (queue.isNotEmpty() && endNode == null) {
        val (node, score) = queue.poll()
        if (endFn(node)) endNode = node
        for (neighbour in neighboursFn(node)) {
            if (!seen.containsKey(neighbour)) {
                val scoredNode = ScoredNode(neighbour, 1 + score)
                queue.add(scoredNode)
                seen[scoredNode.node] = PathToNode(node, scoredNode.score)
            }
        }
    }
    return ShortestPath(start, seen, endNode)
}

private data class ScoredNode<K>(val node: K, val score: Int)
data class PathToNode<K>(val from: K, val score: Int)

class ShortestPath<K>(
    val start: K,
    private val paths: Map<K, PathToNode<K>>,
    val end: K?
) {

    fun getScore(): Int? {
        return paths[end]?.score
    }

    fun getPath(): List<K> {
        val result = mutableListOf<K>()
        var x = end
        if (end == null) return emptyList()
        while (x != start) {
            result.add(x!!)
            x = paths[x]?.from
        }
        return result.reversed()
    }
}

fun <K, V> multiMap() = mutableMapOf<K, LinkedList<V>>()
fun <K, V> MutableMap<K, LinkedList<V>>.add(k: K, v: V) = this.computeIfAbsent(k) { _ -> LinkedList() }.add(v)

class Timer(val name: String = "Time") {
    private val start = System.currentTimeMillis()
    private var lastSegment = start

    fun measure(what: String) {
        val time = System.currentTimeMillis()
        if (lastSegment == start)
            println("$name ${time - lastSegment} ms : $what")
        else
            println("$name ${time - lastSegment} / ${(time - start)} ms : $what")
        lastSegment = time
    }

    fun <T> measure(what: String, fn: () -> T): T {
        val result = fn()
        measure(what)
        return result
    }
}

fun <T> permute(list: List<T>, permutationFn: (List<T>) -> Boolean): List<T> {
    val result = AtomicReference<List<T>>()
    permute(ArrayList(list), 0, permutationFn, result)
    return result.get()
}

private fun <T> permute(list: List<T>, k: Int, fn: (List<T>) -> Boolean, result: AtomicReference<List<T>>) {
    if (result.get() != null) return
    for (i in k until list.size) {
        Collections.swap(list, i, k)
        if (result.get() != null) break
        permute(list, k + 1, fn, result)
        Collections.swap(list, k, i)
    }
    if (k == list.size - 1) {
        if (fn(list)) result.set(ArrayList(list))
    }
}