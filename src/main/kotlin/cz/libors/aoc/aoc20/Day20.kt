package cz.libors.aoc.aoc20

import cz.libors.util.*
import cz.libors.util.Vector
import java.util.*
import kotlin.math.abs

@Day("Jurassic Jigsaw")
object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input20.txt").splitByEmptyLine().map { block -> toTile(block) }
        println(task1(input))
        println(task2(input))
    }

    private fun task2(tiles: List<Tile>): Int {
        val finalMap = combineTiles(findEdgeFits(tiles), tiles)
        var m = finalMap
        for (i in 1..9) {
            if (i == 5) m = flip(m).toSet() else m = rotate(m).toSet()
            val monsters = findMonsters(m)
            if (monsters.isNotEmpty()) {
                return finalMap.size - monsters.size
            }
        }
        throw IllegalArgumentException("no monster found")
    }

    private fun findMonsters(map: Set<Point>): Set<Point> {
        val monster = """
                            # 
          #    ##    ##    ###
           #  #  #  #  #  #   
        """.trimIndent().splitByNewLine().toPointsWithValue().filter { it.second == '#' }.map { it.first }
        val result = mutableSetOf<Point>()
        val mapBox = map.boundingBox()
        val monsterBoxSize = monster.boundingBox().size()
        for (x in mapBox.first.x until mapBox.second.x - monsterBoxSize.first) {
            for (y in mapBox.first.y until mapBox.second.y - monsterBoxSize.second) {
                val shiftedMonster = monster.map { it + Vector(x, y) }
                if (map.containsAll(shiftedMonster)) {
                    result.addAll(shiftedMonster)
                }
            }
        }
        return result
    }


    private fun combineTiles(edgeFits: Map<Int, List<Int>>, tiles: List<Tile>): Set<Point> {
        val queue = LinkedList<Int>()
        val resolved = mutableMapOf<Int, Pair<Tile, Vector>>()

        queue.addLast(tiles[0].id)
        resolved[tiles[0].id] = Pair(tiles[0], Vector(0, 0))

        while (queue.isNotEmpty()) {
            val t = queue.removeFirst()
            val (tile, shift) = resolved[t]!!
            for (neighbor in edgeFits[t]!!.filter { !resolved.containsKey(it) }) {
                val newTile = resolveConnection(tile, shift, tiles.find { it.id == neighbor }!!)
                resolved[neighbor] = newTile
                queue.add(neighbor)
            }
        }

        return createMap(resolved.values)
    }

    private fun createMap(parts: Iterable<Pair<Tile, Vector>>): Set<Point> {
        val newBox = Box(Point(1, 1), Point(8, 8))
        return parts.flatMap { part ->
            part.first.points.filter { p -> newBox.contains(p) }.map { it + (part.second * 8) }
        }.toSet()
    }

    private fun resolveConnection(from: Tile, shift: Vector, to: Tile): Pair<Tile, Vector> {
        val fromEdges = edges(from.points)
        val f = fits(fromEdges, edges(to.points) + edges(to.points).map { flip(it) })
        val dir = listOf(Vector.UP, Vector.RIGHT, Vector.DOWN, Vector.LEFT)[f!!.first] + shift
        var p = to.points

        for (i in 1..9) {
            p = if (i != 5) rotate(p) else flip(p)
            val rotFit = fits(fromEdges, edges(p))
            if (rotFit != null && rotFit.first != rotFit.second && abs(rotFit.first - rotFit.second) == 2) {
                return Pair(Tile(to.id, p), dir)
            }
        }
        throw IllegalArgumentException("no side match")
    }

    private fun task1(tiles: List<Tile>) = findEdgeFits(tiles)
        .filter { it.value.size == 2 }
        .keys.fold(1L) { a, b -> a * b }

    private fun findEdgeFits(tiles: List<Tile>): MultiMap<Int, Int> {
        val edges = tiles.map { edges(it.points) }
        val fits = multiMap<Int, Int>()
        for (i in 0 until edges.size - 1) {
            val iEdges = edges[i] + edges[i].map { flip(it) }
            for (j in i + 1 until edges.size) {
                val fitResult = fits(iEdges, edges[j])
                if (fitResult != null) {
                    fits.add(tiles[i].id, tiles[j].id)
                    fits.add(tiles[j].id, tiles[i].id)
                }
            }
        }
        return fits
    }

    private fun fits(a: List<Set<Int>>, b: List<Set<Int>>): Pair<Int, Int>? {
        for (i in a.indices) {
            for (j in b.indices) {
                if (a[i] == b[j]) return Pair(i, j)
            }
        }
        return null
    }

    private fun edges(points: List<Point>): List<Set<Int>> = listOf(
        points.filter { it.y == 0 }.map { it.x }.toSet(), // up
        points.filter { it.x == 9 }.map { it.y }.toSet(),  // right
        points.filter { it.y == 9 }.map { it.x }.toSet(), // down
        points.filter { it.x == 0 }.map { it.y }.toSet() // left
    )

    private fun flip(points: Set<Int>) = points.map { 9 - it }.toSet()

    private fun toTile(block: String): Tile {
        val lines = block.splitByNewLine()
        val id = lines[0].findInts().single()
        val points = lines.drop(1).toPointsWithValue().filter { it.second == '#' }.map { it.first }
        return Tile(id, points)
    }

    private data class Tile(val id: Int, val points: List<Point>)
}