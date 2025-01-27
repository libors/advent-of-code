package cz.libors.util

import java.security.MessageDigest
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Day(val name: String)

typealias IntSet = Int

fun IntSet.set(i: Int): IntSet = this or (1 shl i)
fun IntSet.isSet(i: Int): Boolean = (this and (1 shl i)) != 0
fun IntSet.isNotSet(i: Int): Boolean = (this and (1 shl i)) == 0
fun IntSet.set(i: Int, value: Int) = this or (value shl i)
fun setAllUpTo(n: Int): IntSet = (1 shl n) - 1
fun IntSet.countSet() = Integer.bitCount(this)

private fun pow10(n: Int): Int {
    var result = 1
    for (i in 1..n) result *= 10
    return result
}

fun Long.nthDigit(n: Int): Int = (this % pow10(n) / pow10(n - 1)).toInt()

typealias LongSet = Long

fun LongSet.set(i: Int): LongSet = this or (1L shl i)
fun LongSet.unset(i: Int): LongSet = this and (1L shl i).inv()

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

fun String.findLongs(positive: Boolean = false): List<Long> = Regex(if (positive) "[0-9]+" else "-?[0-9]+")
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
fun Double.isLong() = this % 1 == 0.0
fun Long.pow(x: Int) = this.toDouble().pow(x).toLong()
fun Long.pow(x: Long) = this.toDouble().pow(x.toDouble()).toLong()


fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

fun bisectLeft(interval: Pair<Long, Long>, target: Double, function: (Long) -> Double): Long {
    var lo = interval.first
    var hi = interval.second
    while (lo < hi) {
        val middle = (lo + hi) / 2
        val middleValue = function(middle)
        if (middleValue >= target) hi = middle - 1 else lo = middle + 1
    }
    return lo
}

fun <T> List<T>.tail() = when {
    this.isEmpty() -> throw NoSuchElementException()
    else -> this.subList(1, this.size)
}

fun <T> MutableList<T>.swap(i1: Int, i2: Int) {
    val tmp = this[i1]
    this[i1] = this[i2]
    this[i2] = tmp
}

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

fun rotate(points: Iterable<Point>) = rotate90(points).let { r -> points.map { r(it) } }

fun flip(points: Iterable<Point>): List<Point> {
    val b = points.boundingBox()
    return points.map { Point(b.second.x + b.first.x - it.x, it.y) }
}

private fun rotate90(points: Iterable<Point>): (Point) -> Point {
    val box = points.boundingBox()
    val size = box.size()
    val newMaxX = box.first.x + size.second - 1
    return { (x, y) ->
        val sx = x - box.first.x
        val sy = y - box.first.y
        Point(newMaxX - sy, box.first.y + sx)
    }
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
    fun neighbours(alsoDiag: Boolean = false) = if (alsoDiag) adjacentPoints() + diagonalPoints() else adjacentPoints()
    private fun adjacentPoints(): List<Point> =
        listOf(add(Vector.UP), add(Vector.DOWN), add(Vector.LEFT), add(Vector.RIGHT))

    fun diagonalPoints(): List<Point> =
        listOf(add(Vector.RIGHT_DOWN), add(Vector.RIGHT_UP), add(Vector.LEFT_DOWN), add(Vector.LEFT_UP))

    inline fun series(direction: Vector, includeStart: Boolean = false, isValid: (Point) -> Boolean): List<Point> {
        val result = mutableListOf(this)
        if (includeStart) {
            result.add(this)
        }
        var p = this.plus(direction)
        while (isValid(p)) {
            result.add(p)
            p = p.plus(direction)
        }
        return result
    }

    override fun toString() = "[$x, $y]"
    fun coordsString() = "$x,$y"
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

fun Box.invert(points: Set<Point>): Set<Point> {
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
    return current[0]
}

data class TreeNode<T>(val bracket: Char? = '[', val v: T? = null, val items: List<TreeNode<T>> = listOf()) {
    fun isValue() = v != null
    override fun toString(): String {
        return v?.toString() ?: "[${items.joinToString(",")}]"
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
fun List<Int>.toPoint3() = Point3(this[0], this[1], this[2])

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

        fun from(s: String, mandatory: Boolean = true): Vector? = when (s.lowercase()) {
            "n", "north", "up", "u", "^" -> UP
            "e", "east", "right", "r", ">" -> RIGHT
            "s", "south", "down", "d", "v" -> DOWN
            "w", "west", "left", "l", "<" -> LEFT
            else -> if (mandatory) throw IllegalArgumentException("Unknown vector: '$s'") else null
        }

        fun orthogonalVectors(): List<Vector> = listOf(UP, RIGHT, DOWN, LEFT)
        fun diagonalVectors(): List<Vector> = listOf(RIGHT_UP, RIGHT_DOWN, LEFT_DOWN, LEFT_UP)
    }

    fun normalize(): Vector = gcd(x, y).absoluteValue
        .let { if (it == 0) this else Vector(x / it, y / it) }

    fun turnLeft() = Vector(y, -x)
    fun turnRight() = Vector(-y, x)
    fun negative() = Vector(-x, -y)
    operator fun times(factor: Int) = Vector(x * factor, y * factor)
    fun add(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun unaryMinus() = Vector(-x, -y)
    operator fun plus(other: Vector) = this.add(other)
    fun manhattanDistance() = abs(x) + abs(y)
}

data class Vector3(val x: Int, val y: Int, val z: Int) {
    fun normalize(): Vector3 = gcd(z, gcd(x, y)).absoluteValue
        .let { if (it == 0) this else Vector3(x / it, y / it, z / it) }
    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun unaryMinus() = Vector3(-x, -y, -z)
}

data class Point3(val x: Int, val y: Int, val z: Int) {

    fun adjacent() = listOf(
        Point3(x, y, z + 1),
        Point3(x, y, z - 1),
        Point3(x, y + 1, z),
        Point3(x, y - 1, z),
        Point3(x + 1, y, z),
        Point3(x - 1, y, z)
    )

    fun diagonal() = listOf(
        Point3(x + 1, y, z + 1),
        Point3(x + 1, y, z - 1),
        Point3(x - 1, y, z + 1),
        Point3(x - 1, y, z - 1),
        Point3(x + 1, y + 1, z),
        Point3(x + 1, y - 1, z),
        Point3(x - 1, y + 1, z),
        Point3(x - 1, y - 1, z),
        Point3(x, y + 1, z + 1),
        Point3(x, y + 1, z - 1),
        Point3(x, y - 1, z + 1),
        Point3(x, y - 1, z - 1),
        Point3(x + 1, y + 1, z + 1),
        Point3(x + 1, y - 1, z + 1),
        Point3(x + 1, y + 1, z - 1),
        Point3(x + 1, y - 1, z - 1),
        Point3(x - 1, y + 1, z + 1),
        Point3(x - 1, y - 1, z + 1),
        Point3(x - 1, y + 1, z - 1),
        Point3(x - 1, y - 1, z - 1),
    )

    fun neighbours(alsoDiag: Boolean = false) = if (alsoDiag) adjacent() + diagonal() else adjacent()

    operator fun plus(v: Vector3) = Point3(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector3) = Point3(x - v.x, y - v.y, z - v.z)

    fun toVector() = Vector3(x, y, z)

    fun inRect(min: Point3, max: Point3) =
        x in min.x..max.x && y in min.y..max.y && z in min.z..max.z

    override fun toString() = "[$x, $y, $z]"
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

fun dividePoints(points: Iterable<Point>, isNeighbor: (Point, Point) -> Boolean): List<Set<Point>> {
    val processed = mutableSetOf<Point>()
    val result = mutableListOf<Set<Point>>()
    for (p in points) {
        if (!processed.contains(p)) {
            val area = flood(p) { it.neighbours().filter { n -> isNeighbor(it, n) } }
            result.add(area)
            processed.addAll(area)
        }
    }
    return result
}

data class Interval(val from: Long, val to: Long) {
    fun isSubOf(other: Interval) = from >= other.from && to <= other.to
    fun contains(other: Interval) = other.isSubOf(this)
    fun contains(num: Long) = from <= num && to >= num
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

fun debug(message: String) {
    if (DEBUG_ON) {
        println(message)
    }
}

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

fun <K> dijkstraToAll(
    start: K,
    neighboursFn: (K) -> Iterable<Pair<K, Int>>
): ShortestPaths<K> {
    val paths = dijkstra(start, { _ -> false }, neighboursFn)
    return ShortestPaths(start, paths.paths)
}

fun <K> dijkstraToAll(
    start: K,
    neighboursFn: (K) -> Iterable<K>,
    distanceFn: (K, K) -> Int
): ShortestPaths<K> {
    val paths = dijkstra(start, { _ -> false }, { x -> neighboursFn(x).map { n -> n to distanceFn(x, n) } })
    return ShortestPaths(start, paths.paths)
}

fun <K> dijkstra(
    start: K,
    endFn: (K) -> Boolean,
    neighboursFn: (K) -> Iterable<Pair<K, Int>>
): ShortestPath<K> {
    val queue = PriorityQueue(compareBy<ScoredNode<K>> { it.score })
    val seen = mutableMapOf<K, PathToNode<K>>()
    seen[start] = PathToNode(start, 0)
    queue.add(ScoredNode(start, 0))
    var endNode: K? = null
    while (queue.isNotEmpty() && endNode == null) {
        val (node, score) = queue.poll()
        if (endFn(node)) endNode = node
        for ((neighbour, distance) in neighboursFn(node)) {
            val n = seen[neighbour]
            val newScore = distance + score
            if (n == null || n.score > newScore) {
                val scoredNode = ScoredNode(neighbour, newScore)
                queue.add(scoredNode)
                seen[scoredNode.node] = PathToNode(node, scoredNode.score)
            }
        }
    }
    return ShortestPath(start, seen, endNode)
}

fun <K> bfsToAll(
    start: K,
    neighboursFn: (K) -> Iterable<K>
): ShortestPaths<K> {
    val paths = bfs(start, { _ -> false }, neighboursFn)
    return ShortestPaths(start, paths.paths)
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

class ShortestPaths<K>(
    val start: K,
    private val paths: Map<K, PathToNode<K>>
) {

    fun pathTo(node: K) = ShortestPath(start, paths, node)
    fun distances() = paths.mapValues { it.value.score }
}

class ShortestPath<K>(
    val start: K,
    val paths: Map<K, PathToNode<K>>,
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

    fun hasPath() = getScore() != null
    override fun toString() = "Path $start -> $end : ${getScore()}"
}

typealias MultiMap<K, V> = MutableMap<K, LinkedList<V>>

fun <K, V> multiMap() = mutableMapOf<K, LinkedList<V>>()
fun <K, V> MultiMap<K, V>.add(k: K, v: V) = this.computeIfAbsent(k) { _ -> LinkedList() }.add(v)

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

fun <T> permutations(array: Array<T>): Iterable<Array<T>> {
    fun permute(array: Array<T>, left: Int, right: Int, variants: MutableList<Array<T>>) {
        if (left == right) {
            variants.add(array.clone())
        } else {
            for (i in left..right) {
                swap(array, left, i)
                permute(array, left + 1, right, variants)
                swap(array, left, i)
            }
        }
    }

    val result = mutableListOf<Array<T>>()
    permute(array.clone(), 0, array.size - 1, result)
    return result
}

private fun <T> swap(array: Array<T>, idx1: Int, idx2: Int) {
    array[idx1] = array[idx2].also { array[idx2] = array[idx1] }
}

fun combinations(m: Int, n: Int): Iterable<IntArray> {
    val combination = IntArray(m)
    val variants = ArrayList<IntArray>()
    fun generate(k: Int) {
        if (k >= m) {
            val variant = IntArray(m)
            for (i in 0 until m) variant[i] = combination[i]
            variants.add(variant)
        } else {
            for (j in 0 until n)
                if (k == 0 || j > combination[k - 1]) {
                    combination[k] = j
                    generate(k + 1)
                }
        }
    }
    generate(0)
    return variants
}

fun permute(n: Int): List<IntArray> {
    val result = mutableListOf<IntArray>()
    val array = IntArray(n) { it }

    fun permute(size: Int) {
        if (size == 1) result.add(array.copyOf())
        for (i in 0 until size) {
            permute(size - 1)
            if (size % 2 == 1) {
                val temp = array[0];
                array[0] = array[size - 1];
                array[size - 1] = temp;
            } else {
                val temp = array[i];
                array[i] = array[size - 1];
                array[size - 1] = temp;
            }
        }
    }

    permute(n)
    return result
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
    if (k == list.size - 1 && fn(list)) result.set(ArrayList(list))
}

private val md = MessageDigest.getInstance("MD5")

@OptIn(ExperimentalStdlibApi::class)
fun md5(s: String) = md.digest(s.toByteArray()).toHexString()

class Composite<T>(val item: T, val children: MutableList<Composite<T>> = mutableListOf()) {
    fun add(item: T) {
        children.add(Composite(item))
    }

    fun add(composite: Composite<T>) {
        children.add(composite)
    }

    override fun toString() = "{$item: [${children.joinToString(",")}]}"
}

class CNode<T>(var value: T) {
    var left: CNode<T> = this
    var right: CNode<T> = this

    fun isOnly() = left == this

    fun find(fn: (T) -> Boolean): CNode<T> {
        if (fn(this.value)) return this
        var x = this.right
        while (x != this) {
            if (fn(x.value)) return x
            x = x.right
        }
        throw NoSuchElementException()
    }

    fun move(x: Int): CNode<T> {
        var n = this
        if (x > 0) {
            for (i in 1..x) n = n.right
        } else {
            for (i in 1..-x) n = n.left
        }
        return n
    }

    fun removeGetRight(): CNode<T> {
        require(!isOnly())
        val result = this.right
        this.right.left = left
        this.left.right = right
        this.right = this
        this.left = this
        return result
    }

    fun addRight(value: T): CNode<T> {
        val rightNode = CNode(value)
        rightNode.right = right
        rightNode.left = this
        right.left = rightNode
        right = rightNode
        return rightNode
    }

    fun addLeft(value: T): CNode<T> {
        val leftNode = CNode(value)
        leftNode.right = this
        leftNode.left = left
        left.right = leftNode
        left = leftNode
        return leftNode
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("[")
        sb.append(value)
        var x = right
        while (x != this) {
            sb.append(", ")
            sb.append(x.value)
            x = x.right
        }
        sb.append("]")
        return sb.toString()
    }
}
