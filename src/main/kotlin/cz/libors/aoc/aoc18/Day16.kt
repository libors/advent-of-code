package cz.libors.aoc.aoc18

import cz.libors.util.*

typealias OpcodeFn = (Int, Int) -> Int

@Day("Chronal Classification")
object Day16 {

    private val opcodes = listOf(
        Opcode("addr", Int::plus, "rr"),
        Opcode("addi", Int::plus, "ri"),
        Opcode("mulr", Int::times, "rr"),
        Opcode("muli", Int::times, "ri"),
        Opcode("banr", Int::and, "rr"),
        Opcode("bani", Int::and, "ri"),
        Opcode("borr", Int::or, "rr"),
        Opcode("bori", Int::or, "ri"),
        Opcode("setr", { a, _ -> a }, "ri"),
        Opcode("seti", { a, _ -> a }, "ii"),
        Opcode("gtir", ::gt, "ir"),
        Opcode("gtri", ::gt, "ri"),
        Opcode("gtrr", ::gt, "rr"),
        Opcode("eqir", ::eq, "ir"),
        Opcode("eqri", ::eq, "ri"),
        Opcode("eqrr", ::eq, "rr")
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input16.txt").split(Regex("\r?\n".repeat(4)))
        val scenarios = input[0].splitByEmptyLine().map { block ->
            val lines = block.splitByNewLine()
            Scenario(lines[0].findInts(), lines[1].findInts(), lines[2].findInts())
        }
        println(task1(scenarios))
        println(task2(scenarios, input[1]))
    }

    private fun task1(scenarios: List<Scenario>) = scenarios.count { s -> opcodes.count { o -> isPossible(s, o) } >= 3 }

    private fun task2(scenarios: List<Scenario>, program: String): Int {
        val opcodeMap = recognizeOpcodes(scenarios)
        val regs = mutableListOf(0, 0, 0, 0)
        program.splitByNewLine().map { it.findInts() }.forEach { opcodeMap[it[0]]!!.exec(it[1], it[2], it[3], regs) }
        return regs[0]
    }

    private fun recognizeOpcodes(scenarios: List<Scenario>): Map<Int, Opcode> {
        val map = multiMap<Scenario, Opcode>()
        scenarios.forEach { s -> opcodes.forEach { o -> if (isPossible(s, o)) map.add(s, o) } }
        val result = mutableMapOf<Int, Opcode>()
        while (map.isNotEmpty()) {
            val singles = map.filter { it.value.size == 1 }
            for (single in singles) {
                result[single.key.opcode[0]] = single.value.first
                map.remove(single.key)
            }
            val resolvedOpCodes = singles.values.map { it.first }.toSet()
            map.values.forEach { v -> v.removeIf { resolvedOpCodes.contains(it) } }
        }
        return result
    }

    private fun isPossible(scenario: Scenario, opcode: Opcode): Boolean {
        val regs = scenario.before.toMutableList()
        val o = scenario.opcode
        opcode.exec(o[1], o[2], o[3], regs)
        return regs == scenario.after
    }

    private fun gt(a: Int, b: Int) = if (a > b) 1 else 0
    private fun eq(a: Int, b: Int) = if (a == b) 1 else 0

    private data class Opcode(val name: String, val fn: OpcodeFn, val aReg: Boolean, val bBreg: Boolean) {
        constructor(name: String, fn: OpcodeFn, inputs: String) :
                this(name, fn, inputs[0] == 'r', inputs[1] == 'r')

        fun exec(a: Int, b: Int, c: Int, regs: MutableList<Int>) {
            val x = if (aReg) regs[a] else a
            val y = if (bBreg) regs[b] else b
            regs[c] = fn(x, y)
        }

    }

    private data class Scenario(val before: List<Int>, val opcode: List<Int>, val after: List<Int>)
}