package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.findPositiveInts
import cz.libors.util.readToText

@Day("Secure Container")
object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input4.txt").findPositiveInts()
        println(input)
        println(task1(input[0], input[1]))
        println(task2(input[0], input[1]))
    }

    private fun toArray(num: Int) = num.toString().toCharArray().map { it.digitToInt() }.toIntArray()

    private fun task1(from: Int, to: Int) = (from..to)
        .count { num -> toArray(num).let { isNotDecreasing(it) && isRepeat(it) } }

    private fun task2(from: Int, to: Int) = (from..to)
        .count { num -> toArray(num).let { isNotDecreasing(it) && isClearDouble(it) } }

    private fun isNotDecreasing(num: IntArray): Boolean {
        for (i in 0..4)
            if (num[i] > num[i + 1]) return false
        return true
    }

    private fun isRepeat(num: IntArray): Boolean {
        for (i in 0..4)
            if (num[i] == num[i + 1]) return true
        return false
    }

    private fun isClearDouble(num: IntArray): Boolean {
        var curRepeat = 0
        for (i in 0..4) {
            if (num[i] == num[i + 1]) {
                curRepeat++
            } else {
                if (curRepeat == 1) return true else curRepeat = 0
            }
        }
        return curRepeat == 1
    }
}