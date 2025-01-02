package cz.libors.aoc.aoc16

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Security Through Obscurity")
object Day4 {

    private val regex = Regex("(.*)-(\\d+)\\[(.*)]")

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input4.txt").map { line ->
            regex.matchEntire(line)!!.groupValues.let { Room(it[1], it[2].toInt(), it[3]) }
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Room>) = input.filter { isCorrect(it) }.sumOf { it.id }
    private fun task2(input: List<Room>) = input.filter { isCorrect(it) }
        .map { room -> room.name.map { decrypt(it, room.id) }.joinToString("") to room.id }
        .find { it.first == "northpole object storage" }!!.second

    private fun decrypt(ch: Char, num: Int): Char = when (ch) {
        '-' -> ' '
        else -> 'a' + ((ch - 'a' + num) % 26)
    }

    private fun isCorrect(room: Room): Boolean {
        val order = room.name.filter { it.isLetter() }.groupingBy { it }.eachCount().toList()
            .sortedWith(compareBy({ -it.second }, { it.first })).take(5)
            .map { it.first }.joinToString("")
        return order == room.checksum
    }

    private data class Room(val name: String, val id: Int, val checksum: String)
}