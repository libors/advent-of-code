package cz.libors.aoc.aoc16

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines

@Day("Timing is Everything")
object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input15.txt").map {
            line -> line.findInts().let { Disc(it[1], it[3]) }
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Disc>) = findTimeToDropCoin(input)
    private fun task2(input: List<Disc>) = findTimeToDropCoin(input + Disc(11, 0))

    private fun findTimeToDropCoin(discs: List<Disc>): Int {
        var i = 0
        while (true) {
            var ok = true
            for (d in 1..discs.size) {
                val disk = discs[d - 1]
                if ((disk.start + i + d) % disk.positions != 0) {
                    ok = false
                    break
                }
            }
            if (ok) return i
            i++
        }
    }

    private data class Disc(val positions: Int, val start: Int)
}