package cz.libors.aoc.aoc16

import cz.libors.util.CNode
import cz.libors.util.Day
import cz.libors.util.readToText

@Day("An Elephant Named Joseph")
object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input19.txt").toInt()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(num: Int): Any {
        val a = IntArray(num) { 1 }
        var i = 0
        while (true) {
            if (a[i % num] > 0) {
                var j = i + 1
                while (a[j % num] == 0) j++
                val sum = a[i % num] + a[j % num]
                if (sum == num) return i % num + 1
                a[i % num] = sum
                a[j % num] = 0
            }
            i++
        }
    }

    private fun task2(input: Int): Int {
        var n = CNode(1)
        for (i in 2..input) n.addLeft(i)
        val opNum = input / 2 + 1
        var opposite = n.find { it == opNum }
        var move = input % 2 != 0
        while (!n.isOnly()) {
            opposite = opposite.removeGetRight()
            n = n.right
            if (move) opposite = opposite.right
            move = !move
        }
        return n.value
    }
}