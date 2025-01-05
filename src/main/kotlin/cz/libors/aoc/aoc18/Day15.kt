package cz.libors.aoc.aoc18

import cz.libors.util.*
import java.awt.Color

@Day("Beverage Bandits")
object Day15 {

    private val g = Graphics(
        debugFromStart = false,
        colorSchema = ColorSchemas.staticColors(listOf(Color.WHITE, Color.LIGHT_GRAY, Color.YELLOW, Color.GREEN)),
        charOrder = ".#GE"
    )

    private val pointOrder = compareBy<Point>({ it.y }, { it.x })
    private val warriorOrder = compareBy<Warrior>({ it.pos.y }, { it.pos.x })
    private val targetOrder = compareBy<Warrior>({ it.hitPoints }, { it.pos.y }, { it.pos.x })

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input15.txt").toPointsWithValue()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Pair<Point, Char>>) = Game(input, 3).play().score()

    private fun task2(input: List<Pair<Point, Char>>): Int {
        val elves = input.count { it.second == 'E' }
        val attackPower = 1 + bisectLeft(Pair(4L, 100L), elves.toDouble()) {
            (elves - Game(input, it.toInt()).play().deadElves()).toDouble()
        }
        return Game(input, attackPower.toInt()).play().score()
    }

    private class Game(input: List<Pair<Point, Char>>, elfAttackPower: Int) {
        val warriors = input.filter { "GE".contains(it.second) }
            .map { Warrior(it.second == 'E', it.first, attackPower = if (it.second == 'E') elfAttackPower else 3) }
            .associateBy { it.pos }.toMutableMap()
        val map = input.toMap().toMutableMap()
        var goblins = warriors.values.count { !it.elf }
        var elves = warriors.values.count { it.elf }
        val startElves = elves
        var result: Pair<Int, Int>? = null

        fun play(): Game {
            var roundsCompleted = 0
            var completedInTheMiddle = false
            while (goblins > 0 && elves > 0) {
                //g.showChars(map, percents = warriors.mapValues { it.value.hitPoints / 2 })
                val warriorsToPlay = warriors.values.sortedWith(warriorOrder)
                for (warrior in warriorsToPlay) {
                    if (warrior.hitPoints > 0 && goblins > 0 && elves > 0) {
                        val target = findTargetInRange(warrior)
                        if (target != null) {
                            attack(warrior, target)
                        } else {
                            val toGo = findWayPoint(warrior)
                            if (toGo != null) {
                                move(warrior.pos, toGo)
                                val newTarget = findTargetInRange(warrior)
                                if (newTarget != null) attack(warrior, newTarget)
                            }
                        }
                    } else {
                        if (goblins == 0 || elves == 0) completedInTheMiddle = true
                    }
                }
                if (!completedInTheMiddle) roundsCompleted++
            }
            result = Pair(roundsCompleted * warriors.values.sumOf { it.hitPoints },
                startElves - warriors.count { it.value.elf })
            return this
        }

        fun score() = result!!.first
        fun deadElves() = result!!.second

        private fun attack(w: Warrior, target: Warrior) {
            target.hitPoints -= w.attackPower
            if (target.hitPoints <= 0) kill(target)
        }

        private fun kill(w: Warrior) {
            map[w.pos] = '.'
            warriors.remove(w.pos)
            if (w.elf) elves-- else goblins--
        }

        private fun findWayPoint(w: Warrior): Point? {
            val inRangePoints = warriors.values.filter { it.elf != w.elf }.flatMap { it.pos.neighbours() }
                .filter { map[it] == '.' }
                .toSet()
            val paths = bfsToAll(w.pos) { it.neighbours().filter { n -> map[n] == '.' } }
            val distances = paths.distances().filterKeys { inRangePoints.contains(it) }
            if (distances.isEmpty()) return null
            val minDistance = distances.minOf { it.value }
            val nearestTarget = distances.filter { it.value == minDistance }.keys.sortedWith(pointOrder).first()
            // bfs back to attacker neighbors to find correct path among multiple best paths
            return bfsToAll(nearestTarget) { it.neighbours().filter { n -> map[n] == '.' } }.distances()
                .filter { it.value == minDistance - 1 && it.key in w.pos.neighbours() }
                .keys.sortedWith(pointOrder).first()
        }

        private fun move(from: Point, to: Point) {
            val w = warriors[from]!!
            warriors.remove(from)
            w.pos = to
            warriors[to] = w
            map[from] = '.'
            map[to] = if (w.elf) 'E' else 'G'
        }

        private fun findTargetInRange(w: Warrior) = w.pos.neighbours()
            .mapNotNull { warriors[it] }
            .filter { it.elf != w.elf }
            .sortedWith(targetOrder)
            .firstOrNull()
    }

    private data class Warrior(val elf: Boolean, var pos: Point, var hitPoints: Int = 200, val attackPower: Int = 3)
}