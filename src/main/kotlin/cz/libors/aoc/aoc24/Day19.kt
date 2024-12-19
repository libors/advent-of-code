package cz.libors.aoc.aoc24

import cz.libors.util.Day
import cz.libors.util.readToText
import cz.libors.util.splitByEmptyLine
import cz.libors.util.splitByNewLine

@Day("Linen Layout")
object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input19.txt").splitByEmptyLine()
        val options = input[0].split(", ")
        val designs = input[1].splitByNewLine()

        val counts = findCounts(options, designs)
        println(task1(counts))
        println(task2(counts))
    }

    private fun task1(counts: List<Long>) = counts.count { it > 0 }
    private fun task2(counts: List<Long>) = counts.sum()

    private fun findCounts(options: List<String>, designs: List<String>) = designs.map { Finder(it, options).count() }

    class Finder(private val design: String, private val options: List<String>) {
        private val memo = Array(design.length + 1) { -1L }

        fun count() = find(0)

        private fun find(soFar: Int): Long {
            val x = memo[soFar]
            if (x >= 0) return x
            if (soFar == design.length) return 1

            var sum = 0L
            for (o in options)
                if (soFar + o.length <= design.length && o == design.substring(soFar, soFar + o.length))
                    sum += find(soFar + o.length)

            memo[soFar] = sum
            return sum
        }
    }
}