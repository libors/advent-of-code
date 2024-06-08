package cz.libors.aoc.aoc23

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines

@Day(name = "Cube Conundrum")
object Day2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input2.txt").map { line ->
            val parts = line.split(":")
            val attempts = parts[1].split(";").map { a ->
                a.trim().split(", ").associate {
                    it.substringAfter(" ").trim() to it.substringBefore(" ").trim().toInt()
                }
            }
            Game(parts[0].findInts().first(), attempts)
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(games: List<Game>): Int {
        val max = mapOf("red" to 12, "green" to 13, "blue" to 14)
        return games.filter { g -> g.attempts.all { a -> isSubset(a, max) } }.sumOf { it.id }
    }

    private fun task2(games: List<Game>) = games.sumOf { g -> g.attempts.maxOf {
        it["red"] ?: 0 } * g.attempts.maxOf { it["green"] ?: 0 } * g.attempts.maxOf { it["blue"] ?: 0 }
    }

    private fun isSubset(sub: Map<String, Int>, of: Map<String, Int>) = sub.all { e -> of[e.key]!! >= e.value }

    private data class Game(val id: Int, val attempts: List<Map<String, Int>>)
}