package cz.libors.aoc.aoc23

import cz.libors.util.*
import java.util.LinkedList

@Day(name = "Sand Slabs")
object Day22 {

    val DOWN = Vector3(0, 0, -1)

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input22.txt")
            .map { line -> line.findInts().let { Pair(Point3(it[0], it[1], it[2]), Point3(it[3], it[4], it[5])) } }
            .map { createBrick(it) }
        val settled = settle(input)
        val relations = findRelations(settled)

        println(task1(relations))
        println(task2(relations))
    }

    private fun task1(relations: List<Relation>) =
        relations.filter { it.holding.all { holding -> relations[holding].heldBy.size > 1 } }.size

    private fun task2(relations: List<Relation>) = relations.indices.sumOf { disintegratedFor(relations, it) }

    private fun findRelations(settled: List<List<Point3>>): List<Relation> {
        val points = mutableMapOf<Point3, Int>()
        settled.forEachIndexed { idx, list -> list.forEach { points[it] = idx } }
        val relations = settled.mapIndexed { idx, brick ->
            val holding = brick.map { it - DOWN }.map { points[it] }.filter { it != idx }.filterNotNull().toSet()
            val heldBy = brick.map { it + DOWN }.map { points[it] }.filter { it != idx }.filterNotNull().toSet()
            Relation(idx, holding, heldBy)
        }
        return relations
    }

    private fun settle(input: List<List<Point3>>): List<List<Point3>> {
        val settledPoints = mutableSetOf<Point3>()
        val allPoints = input.flatten().toMutableSet()
        val falling = LinkedList(input)
        val settled = mutableListOf<List<Point3>>()

        while (falling.isNotEmpty()) {
            var brick = falling.removeFirst()
            while (brick.map { it + DOWN }.filter { !brick.contains(it) }
                    .none { it.z == 0 || allPoints.contains(it) }) {
                allPoints.removeAll(brick)
                brick = brick.map { it + DOWN }
                allPoints.addAll(brick)
            }
            val downPoints = brick.map { it + DOWN }
            if (downPoints.any { it.z == 0 || settledPoints.contains(it) }) {
                settledPoints.addAll(brick)
                settled.add(brick)
            } else {
                falling.addLast(brick)
            }
        }
        return settled
    }

    private fun createBrick(ends: Pair<Point3, Point3>): List<Point3> {
        val v = (ends.first - ends.second.toVector()).toVector().normalize()
        var point = ends.first
        val result = mutableListOf(point)
        do {
            point -= v
            result.add(point)
        } while (point != ends.second)
        return result
    }

    private fun disintegratedFor(relations: List<Relation>, brickId: Int): Int {
        val queue = LinkedList<Int>()
        queue.addLast(brickId)
        val disintegrated = mutableSetOf(brickId)
        while (queue.isNotEmpty()) {
            val current = relations[queue.removeFirst()]
            if (current.heldBy.none { !disintegrated.contains(it) }) {
                disintegrated.add(current.id)
            }
            current.holding.forEach { queue.addLast(it) }
        }
        return disintegrated.size - 1
    }

    private data class Relation(val id: Int, val holding: Set<Int>, val heldBy: Set<Int>)

}