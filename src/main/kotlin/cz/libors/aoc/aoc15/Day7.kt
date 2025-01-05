package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.findAlphanums
import cz.libors.util.readToLines

@Day("Some Assembly Required")
object Day7 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input7.txt").map { convertToOp(it) }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Op>) = Computer(input).get("a")
    private fun task2(input: List<Op>): UShort {
        val a = Computer(input).get("a")
        val newOps = input.map { if (it.output != "b") it else Op(listOf(), "b") { a } }
        return Computer(newOps).get("a")
    }

    private class Computer(input: List<Op>) {
        private val opByOutput = input.associateBy { it.output }
        private val memo = mutableMapOf<String, UShort> ()

        fun get(s: String): UShort {
            val fromMemo = memo[s]
            if (fromMemo != null) return fromMemo
            val op = opByOutput[s]
            if (op == null) return s.toUShort()
            val result = op.fn(op.input.map { get(it) })
            memo[s] = result
            return result
        }
    }

    private fun convertToOp(s: String): Op {
        val parts = s.findAlphanums()
        return when {
            s.contains("AND") -> Op(listOf(parts[0], parts[2]), parts[3]) { it[0] and it[1] }
            s.contains("OR") -> Op(listOf(parts[0], parts[2]), parts[3]) { it[0] or it[1] }
            s.contains("LSHIFT") -> Op(listOf(parts[0], parts[2]), parts[3]) { (it[0].toUInt() shl it[1].toInt()).toUShort()}
            s.contains("RSHIFT") -> Op(listOf(parts[0], parts[2]), parts[3]) { (it[0].toUInt() shr it[1].toInt()).toUShort()}
            s.contains("NOT") -> Op(listOf(parts[1]), parts[2]) { it[0].inv() }
            s.toIntOrNull() != null -> Op(listOf(), parts[1]) { parts[0].toUShort()}
            else -> Op(listOf(parts[0]), parts[1]) { it[0] }
        }
    }

    private data class Op(val input: List<String>, val output: String, val fn: (List<UShort>) -> UShort)
}