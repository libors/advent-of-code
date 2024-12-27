package cz.libors.aoc.aoc20

import cz.libors.util.*

private typealias Plan = Map<Point, Char>
private typealias DecisionFn = (Plan, Point) -> Boolean

@Day("Seating System")
object Day11 {

    private val allDirections = Vector.diagonalVectors() + Vector.orthogonalVectors()

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input11.txt").toPointsWithValue().toMap()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: Plan) = iterate(input, ::simpleOccFn, ::simpleEmptyFn)
    private fun task2(input: Plan) = iterate(input, ::complexOccFn, ::complexEmptyFn)

    private fun iterate(plan: Plan, changeOccupiedFn: DecisionFn, changeEmptyFn: DecisionFn): Int {
        var current = plan
        while (true) {
            val iterResult = iteration(current, changeOccupiedFn, changeEmptyFn)
            current = iterResult.first
            if (iterResult.second == 0) return current.count { it.value == '#' }
        }
    }

    private fun simpleOccFn(plan: Plan, seat: Point) = seat.neighbours(true).count { plan[it] == '#' } >= 4
    private fun simpleEmptyFn(plan: Plan, seat: Point) = seat.neighbours(true).count { plan[it] == '#' } == 0
    private fun complexOccFn(plan: Plan, seat: Point) = allDirections.count { seesOccupiedSeat(plan, seat, it) } >= 5
    private fun complexEmptyFn(plan: Plan, seat: Point) = allDirections.count{ seesOccupiedSeat(plan, seat, it)} == 0

    private fun seesOccupiedSeat(plan: Map<Point, Char>, seat: Point, dir: Vector): Boolean {
        var checkPoint = seat + dir
        while (true) {
            val value = plan[checkPoint]
            if (value == '#') return true
            if (value != '.') return false
            checkPoint += dir
        }
    }

    private fun iteration(orig: Plan, changeOccupiedFn: DecisionFn, changeEmptyFn: DecisionFn): Pair<Plan, Int> {
        var changes = 0
        val result = mutableMapOf<Point, Char>()
        for ((seat, value) in orig) {
            when (value) {
                '.' -> result[seat] = value
                'L' -> if (changeEmptyFn(orig, seat)) {
                    result[seat] = '#'
                    changes++
                } else result[seat] = 'L'
                '#' -> if (changeOccupiedFn(orig, seat)) {
                    result[seat] = 'L'
                    changes++
                } else result[seat] = '#'
                else -> throw IllegalArgumentException()
            }
        }
        return Pair(result, changes)
    }
}