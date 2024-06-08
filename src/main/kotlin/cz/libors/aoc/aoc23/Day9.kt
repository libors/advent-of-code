package cz.libors.aoc.aoc23

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines

@Day(name = "Mirage Maintenance")
object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input9.txt").map { it.findInts() }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<List<Int>>) = input.map { createTriangle(it) }.sumOf { findNext(it) }
    private fun task2(input: List<List<Int>>) = input.map { createTriangle(it) }.sumOf { findPrevious(it) }

    private fun findNext(triangle: List<List<Int>>): Int {
        var n = 0
        for (i in triangle.size - 2 downTo 0) {
            n += triangle[i].last()
        }
        return n
    }

    private fun findPrevious(triangle: List<List<Int>>): Int {
        var n = 0
        for (i in triangle.size - 2 downTo 0) {
            n = triangle[i].first() - n
        }
        return n
    }

    private fun createTriangle(it: List<Int>): List<List<Int>> {
        val triangle = mutableListOf(it)
        var list = triangle.last()
        do {
            val newList = (0 until list.size - 1).map { list[it + 1] - list[it] }
            list = newList
            triangle.add(newList)
        } while (newList.any { it != 0 })
        return triangle
    }

}