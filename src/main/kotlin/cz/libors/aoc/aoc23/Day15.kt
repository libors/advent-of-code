package cz.libors.aoc.aoc23

import cz.libors.util.Day
import cz.libors.util.findAlphanums
import cz.libors.util.readToText

@Day(name = "Lens Library")
object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input15.txt").split(",")
        println(task1(input))

        val instructions = input.map {
            val split = it.findAlphanums()
            if (it.contains('=')) Instruction(split[0], '=', split[1].toInt())
            else Instruction(split[0], '-', null)
        }
        println(task2(instructions))
    }

    private fun task2(instructions: List<Instruction>): Int {
        val buckets = MutableList(256) { mutableListOf<Lens>() }
        for (i in instructions) {
            val bucket = buckets[hash(i.label)]
            when (i.op) {
                '-' -> bucket.removeIf { it.id == i.label }
                '=' -> {
                    val idx = bucket.indexOfFirst { it.id == i.label }
                    val lens = Lens(i.label, i.num!!)
                    if (idx == -1) {
                        bucket.add(lens)
                    } else {
                        bucket.removeAt(idx)
                        bucket.add(idx, lens)
                    }
                }
                else -> throw IllegalArgumentException("Unknown op ${i.op}")
            }
        }

        return buckets.flatMapIndexed { bIdx, bucket ->
            bucket.mapIndexed { lIdx, lens -> (bIdx + 1) * (lIdx + 1) * lens.f }
        }.sum()
    }

    private fun task1(input: List<String>) = input.sumOf { hash(it) }

    private fun hash(s: String): Int {
        var result = 0
        s.chars().forEach {
            result += it
            result *= 17
            result %= 256
        }
        return result
    }

    private data class Instruction(val label: String, val op: Char, val num: Int?)
    private data class Lens(val id: String, val f: Int)
}