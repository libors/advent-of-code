package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.findLongs
import cz.libors.util.readToText

@Day("Tractor Beam")
object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input19.txt").findLongs()
        println(task1(input.toLongArray()))
        println(task2(input.toLongArray()))
    }

    private fun task1(code: LongArray): Int {
        var result = 0
        for (i in 0..49)
            for (j in 0..49)
                if (beamOn(i, j, code)) result++
        return result
    }

    private fun task2(code: LongArray): Long {
        val wantedLength = 100
        val rowHistory = HashMap<Int, Pair<Int, Int>>()
        var row = wantedLength
        var min = 0
        var size = 0
        do {
            while (!beamOn(row, min, code)) min++
            while (beamOn(row, min + size, code)) size++

            val before100 = rowHistory[row - (wantedLength - 1)]
            if (before100 != null && before100.second - min >= wantedLength) {
                return min * 10000L + row - (wantedLength - 1)
            }

            rowHistory[row] = Pair(min, min + size)
            row++
            size--
        } while (true)
    }

    private fun beamOn(row: Int, column: Int, code: LongArray) =
        Computer.create(code.copyOf(), input = Input.value(column.toLong(), row.toLong()))
            .runCode().getLastOutput().toInt() == 1
}