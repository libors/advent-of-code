package cz.libors.aoc.aoc16

import cz.libors.util.Day
import cz.libors.util.readToLines

private typealias F = (MutableMap<String, Int>) -> Unit

@Day("Leonardo's Monorail")
object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input12.txt").map { toInstruction(it) }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Instruction>) = Computer(input).execute().let { it["a"] }
    private fun task2(input: List<Instruction>) = Computer(input).update { it["c"] = 1 }.execute().let { it["a"] }

    private class Computer(val instructions: List<Instruction>) {
        val regs = mutableMapOf("a" to 0, "b" to 0, "c" to 0, "d" to 0, "i" to 0)

        fun update(f: F): Computer {
            f(regs)
            return this
        }

        fun execute(): Map<String, Int> {
            while (regs["i"]!! < instructions.size) {
                instructions[regs["i"]!!].execute(regs)
                regs.merge("i", 1, Int::plus)
            }
            return regs
        }
    }

    private fun toInstruction(s: String): Instruction {
        val split = s.split(" ")
        val x = split[1]
        val y = if (split.size == 3) split[2] else ""
        return when (split[0]) {
            "cpy" -> Instruction(s) { r -> r[y] = toNum(x, r) }
            "inc" -> Instruction(s) { it.merge(x, 1, Int::plus) }
            "dec" -> Instruction(s) { it.merge(x, -1, Int::plus) }
            "jnz" -> Instruction(s) { r -> r.merge("i",  if (toNum(x, r) != 0) toNum(y, r) - 1 else 0, Int::plus) }
            else -> throw IllegalArgumentException()
        }
    }

    private fun toNum(value: String, regs: Map<String, Int>) = value.toIntOrNull() ?: regs[value]!!

    private data class Instruction(val desc: String, val f: F) {
        fun execute(regs: MutableMap<String, Int>) = f(regs)
    }
}