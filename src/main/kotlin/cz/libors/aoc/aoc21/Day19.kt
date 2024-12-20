package cz.libors.aoc.aoc21

import cz.libors.util.*

object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("test.txt").splitByEmptyLine()
            .map { section -> section.splitByNewLine().drop(1).map { it.findInts().toPoint() } }

        println(input)
    }



    private fun Point3.projections() = listOf(
        Point3(x, y, z),
        Point3(-x, y, -z),
        Point3(-z, y, x),
        Point3(z, y, -x),
        Point3(-x, z, y),
        Point3(x, z, -y)
    )

    private fun Point3.rotate() = Point3(-y, x, z)
}