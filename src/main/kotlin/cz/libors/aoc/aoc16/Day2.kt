package cz.libors.aoc.aoc16

import cz.libors.util.*

@Day("Part Two")
object Day2 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input2.txt").map { line -> line.map { Vector.from(it.toString())!! } }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<List<Vector>>) = go(input, "123\n456\n789")
    private fun task2(input: List<List<Vector>>) = go(input, "  1  \n 234 \n56789\n ABC \n  D  ")

    private fun go(input: List<List<Vector>>, plan: String): String {
        val buttons = plan.splitByNewLine().toPointsWithValue()
            .filter { it.second != ' ' }
            .toMap()
        var pos = buttons.filter { it.value == '5' }.keys.first()
        val result = StringBuilder()
        for (btnInst in input) {
            for (moveInst in btnInst) {
                val next = pos + moveInst
                if (buttons.containsKey(next)) pos = next
            }
            result.append(buttons[pos])
        }
        return result.toString()
    }
}