package cz.libors.aoc.aoc18

import cz.libors.util.*

@Day("Mine Cart Madness")
object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input13.txt").toPointsWithValue()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Pair<Point, Char>>) = run(input)
    private fun task2(input: List<Pair<Point, Char>>) = run(input, true)

    private fun run(input: List<Pair<Point, Char>>, continueAfterCrash: Boolean = false): String {
        val (cars, grid) = prepare(input)
        var remainingCars = cars
        while (true) {
            val sortedCars = remainingCars.sortedWith(compareBy({ it.pos.y }, { it.pos.x }))
            for (car in sortedCars) {
                if (!continueAfterCrash || remainingCars.contains(car)) {
                    move(car, grid)
                    if (remainingCars.count { it.pos == car.pos } > 1) {
                        if (continueAfterCrash) {
                            remainingCars = remainingCars.filter { it.pos != car.pos }
                            if (remainingCars.size == 1) return remainingCars[0].pos.coordsString()
                        } else {
                            return car.pos.coordsString()
                        }
                    }
                }
            }
        }
    }

    private fun prepare(input: List<Pair<Point, Char>>): Pair<List<Car>, Map<Point, Char>> {
        val cars = input.filter { "<>v^".contains(it.second) }
            .map { Car(it.first, Vector.from(it.second.toString())!!) }
        val grid = input.filter { it.second != ' ' }.associate {
            when (it.second) {
                '^', 'v' -> it.first to '|'
                '>', '<' -> it.first to '-'
                else -> it
            }
        }
        return Pair(cars, grid)
    }

    private fun move(car: Car, grid: Map<Point, Char>) {
        car.pos += car.dir
        val road = grid[car.pos]!!
        if ("/\\+".contains(road)) car.turnOn(road)
    }

    private data class Car(var pos: Point, var dir: Vector, var turnState: Int = 0) {
        fun turnOn(ch: Char) {
            dir = when (ch) {
                '/' -> if (dir.x == 0) dir.turnRight() else dir.turnLeft()
                '\\' -> if (dir.x == 0) dir.turnLeft() else dir.turnRight()
                '+' -> when (turnState++ % 3) {
                    0 -> dir.turnLeft()
                    1 -> dir
                    2 -> dir.turnRight()
                    else -> error("modulo")
                }
                else -> error("unexpected turn char")
            }
        }
    }
}