package cz.libors.aoc.aoc19

import cz.libors.util.*

private typealias Plane = Map<Point, Char>

@Day("Donut Maze")
object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input20.txt").toPointsWithValue().filter { it.second != ' ' }.toMap()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(plane: Plane): Int {
        val portalsByEnter = findPortals(plane).associateBy { it.enter }
        val portalsByName = portalsByEnter.values.groupBy { it.name }
        val start = portalsByName["AA"]!!.single().enter
        val end = portalsByName["ZZ"]!!.single().enter

        return bfs(start, { it == end}) { x ->
            val result = mutableListOf<Point>()
            x.neighbours().filter { plane[it] == '.' }.forEach { result.add(it) }
            val portal = portalsByEnter[x]
            if (portal != null) {
                val portalList = portalsByName[portal.name]!!
                if (portalList.size == 2) {
                    result.add(portalList.first { it != portal }.enter)
                }
            }
            result
        }.getScore()!!
    }

    private fun task2(plane: Plane): Int {
        val portalsByEnter = findPortals(plane).associateBy { it.enter }
        val portalsByName = portalsByEnter.values.groupBy { it.name }
        val start = portalsByName["AA"]!!.single().enter
        val end = portalsByName["ZZ"]!!.single().enter

        return bfs(Pair(0, start), { it == Pair(0, end)}) { x ->
            val result = mutableListOf<Pair<Int, Point>>()
            x.second.neighbours().filter { plane[it] == '.' }.forEach { result.add(Pair(x.first, it)) }

            val portal = portalsByEnter[x.second]
            if (portal != null && portal.isAccessible(x.first)) {
                val portalList = portalsByName[portal.name]!!
                if (portalList.size == 2) {
                    val level = if (portal.outer) x.first - 1 else x.first + 1
                    result.add(Pair(level, portalList.first { it != portal }.enter))
                }
            }
            result
        }.getScore()!!
    }

    private fun findPortals(plane: Plane): List<Portal> {
        val innerPlane = plane.keys.boundingBox().let { Pair(it.first + Vector.RIGHT_DOWN * 3, it.second + Vector.LEFT_UP * 3) }
        return plane.filter { !".# ".contains(it.value) && it.key.neighbours().any { p -> plane[p] == '.' } }
            .map { extractPortal(it.key, plane, !innerPlane.contains(it.key)) }
    }

    private fun extractPortal(p: Point, plane: Plane, outer: Boolean): Portal {
        val enter = p.neighbours().first { plane[it] == '.' }
        val enterVector = p.vectorTo(enter)
        val isSecondLetter = enterVector == Vector.DOWN || enterVector == Vector.RIGHT
        val letter = plane[p]!!.toChar()
        val otherLetter = plane[p - enterVector]!!.toChar()
        val name = if (isSecondLetter) "" + otherLetter + letter else "" + letter + otherLetter
        return Portal(name, enter, outer)
    }

    private data class Portal(val name: String, val enter: Point, val outer: Boolean) {
        fun isAccessible(level: Int): Boolean  {
            if (!outer) return true
            if (level == 0 && (name == "AA" || name == "ZZ")) return true
            if (level > 0 && name != "AA" && name != "ZZ") return true
            return false
        }
    }
}