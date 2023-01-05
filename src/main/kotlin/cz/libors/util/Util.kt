package cz.libors.util

import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.math.absoluteValue

private fun resolveResourcePath(x: String): String {
    val traceLine = Thread.currentThread().stackTrace.first { it.className.contains("aoc.aoc")}
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

fun String.findInts(): List<Int> = Regex("-?[0-9]+")
    .findAll(this)
    .map { it.value.toInt() }
    .toList()

fun String.findPositiveInts(): List<Int> = Regex("[0-9]+")
    .findAll(this)
    .map { it.value.toInt() }
    .toList()

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

    fun add(other: Vector) = Point(x + other.x, y + other.y)
    fun vectorTo(other: Point) = Vector(other.x - x, other.y - y)
    fun switch() = Point(y, x)
    fun adjacentPoints(): List<Point> = listOf(add(Vector.UP), add(Vector.DOWN), add(Vector.LEFT), add(Vector.RIGHT))
    fun diagonalPoints(): List<Point> =
        listOf(add(Vector(1, 1)), add(Vector(1, -1)), add(Vector(-1, 1)), add(Vector(-1, -1)))

    override fun toString() = "[$x, $y]"

}

fun Iterable<Point>.boundingBox() = Pair(
    Point(minOf { it.x }, minOf { it.y }),
    Point(maxOf { it.x }, maxOf { it.y })
)

fun Pair<Point, Point>.invert(points: Set<Point>): Set<Point> {
    val result = mutableSetOf<Point>()
    for (x in this.first.x .. this.second.x)
        for (y in this.first.y .. this.second.y) {
            val p = Point(x, y)
            if (!points.contains(p)) result.add(p)
        }
    return result.toSet()
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

    fun negative() = Vector(-x, -y)
    operator fun times(factor: Int) = Vector(x * factor, y * factor)
    fun manhattanDistance() = abs(x) + abs(y)
}

data class Vector3(val x: Int, val y: Int, val z: Int)
data class Point3(val x: Int, val y: Int, val z: Int) {
    fun adjacent() = listOf(
        Point3(x, y, z + 1),
        Point3(x, y, z - 1),
        Point3(x, y + 1, z),
        Point3(x, y - 1, z),
        Point3(x + 1, y, z),
        Point3(x - 1, y, z)
    )

    operator fun plus(v: Vector3) = Point3(x + v.x, y + v.y, z + v.z)

    fun inRect(min: Point3, max: Point3) =
        x in min.x..max.x && y in min.y..max.y && z in min.z..max.z
}

fun Iterable<Point3>.boundingBox3() = Pair(
    Point3(minOf { it.x }, minOf { it.y }, minOf { it.z }),
    Point3(maxOf { it.x }, maxOf { it.y }, maxOf { it.z })
)

data class Interval(val from: Int, val to: Int) {
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
        fun from(x: Int, y: Int) = if (x <= y) Interval(x, y) else Interval(y, x)
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
