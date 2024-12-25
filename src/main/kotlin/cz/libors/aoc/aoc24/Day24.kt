package cz.libors.aoc.aoc24

import cz.libors.util.*

@Day("Crossed Wires")
object Day24 {

    private const val INPUT_BITS = 44

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input24.txt").splitByEmptyLine()
        val parts = input[0].splitByNewLine().map {
            val parts = it.split(": ")
            Part(parts[0].first(), parts[0].substring(1).toInt(), parts[1] == "1", parts[0])
        }
        val ops = input[1].splitByNewLine().map {
            val split = it.findAlphanums()
            Op(split[0], split[2], split[3], split[1])
        }
        println(task1(parts, ops))
        println(task2(parts, ops)) // tailored for my input only
    }

    private fun task2(parts: List<Part>, operations: List<Op>): String {
        var ops = operations
        val switches = mapOf(Pair("rts", "z07"), Pair("jpj", "z12"), Pair("kgj", "z26"), Pair("vvw", "chv"))
        switches.forEach { ops = switchOutput(ops, it.key, it.value) }
        return switches.flatMap { listOf(it.key, it.value) }.sorted().joinToString(",")

        // Next chunks of code helped me to come up with the result half manually
        // Resolving parts try to find differences from standard full adder composed with AND, OR, XOR, that looks like:
        // XOR(Xi, Yi)=X1-i, AND(Xi, Yi)=A1-i, XOR(C, X1-i)=Zi, AND(C,X1-i)=A2-i, OR(A2-i, A1-i)=C2
        ops = findAdderProblemsForFirstXor(ops)
        ops = findAdderProblemsForFirstAnd(ops)
        findAdderProblmesForSecondXor(ops)
        findAdderProblemsForSecondAnd(ops)
        findLastMissingSwitch(ops, parts)
    }

    private fun findLastMissingSwitch(ops: List<Op>, parts: List<Part>) {
        val outputs = ops.map { it.output }.toMutableSet()
        for (output in outputs) {
            val newOps = switchOutput(ops, "vvw", output)
            if (isAdder(parts, newOps)) println("heureka $output")
        }
    }

    private fun findAdderProblemsForSecondAnd(ops: List<Op>) {
        println("RESOLVING A2")
        for (i in 0..INPUT_BITS) {
            val idx = i.toString().padStart(2, '0')
            val found = ops.filter { it.hasInput("X1-$idx") && it.op == "AND" }
            if (found.size != 1) println("error for $idx: found $found")
        }
    }

    private fun findAdderProblmesForSecondXor(ops: List<Op>) {
        println("RESOLVING X2")
        for (i in 0..INPUT_BITS) {
            val idx = i.toString().padStart(2, '0')
            val found = ops.filter { it.hasInput("X1-$idx") && it.op == "XOR" }
            when (found.size) {
                1 -> if (!found[0].output.startsWith("z")) println("$idx expecting z output: ${found[0]}")
                else -> println("error for $idx: found $found")
            }
        }
    }

    private fun findAdderProblemsForFirstAnd(ops: List<Op>): List<Op> {
        println("RESOLVING A1")
        val a1Map = mutableMapOf<String, String>()
        for (i in 0..INPUT_BITS) {
            val idx = i.toString().padStart(2, '0')
            val found = ops.filter { it.hasInput("x$idx") && it.hasInput("y$idx") && it.op == "AND" }
            when (found.size) {
                1 -> if (found[0].output.startsWith("z")) println("suspicious output: ${found[0]}") else a1Map[found[0].output] =
                    "A1-$idx"

                else -> println("error for $idx")
            }
        }
        return ops.map { it.replace(a1Map) }
    }

    private fun findAdderProblemsForFirstXor(ops: List<Op>): List<Op> {
        println("RESOLVING X1")
        val x1Map = mutableMapOf<String, String>()
        for (i in 0..INPUT_BITS) {
            val idx = i.toString().padStart(2, '0')
            val found = ops.filter { it.hasInput("x$idx") && it.hasInput("y$idx") && it.op == "XOR" }
            when (found.size) {
                1 -> if (found[0].output.startsWith("z")) println("suspicious output: ${found[0]}") else x1Map[found[0].output] =
                    "X1-$idx"
                else -> println("error for $idx")
            }
        }
        return ops.map { it.replace(x1Map) }
    }

    private fun switchOutput(ops: List<Op>, name: String, name2: String): List<Op> = ops.map {
        when (it.output) {
            name -> Op(it.first, it.second, name2, it.op)
            name2 -> Op(it.first, it.second, name, it.op)
            else -> it
        }
    }

    private fun isAdder(parts: List<Part>, ops: List<Op>): Boolean {
        val computed = task1(parts, ops)
        val x = parts.filter { it.type == 'x' }.sortedBy { it.num }.reversed().map { if (it.value) 1 else 0 }
            .joinToString("")
        val y = parts.filter { it.type == 'y' }.sortedBy { it.num }.reversed().map { if (it.value) 1 else 0 }
            .joinToString("")
        val expected = (x.toLong(2) + y.toLong(2))
        return computed == expected
    }

    private fun task1(parts: List<Part>, ops: List<Op>): Long {
        val pMap = parts.associate { it.name to it.value }.toMutableMap()

        var notProcessed = ops.toList()
        var i = 0
        while (notProcessed.isNotEmpty()) {
            if (i++ > 3000) break // prevents endless loop in cycles when resolving part 2
            val onRight = notProcessed.map { it.output }.toSet()
            val toProcess = notProcessed.filter { !onRight.contains(it.first) && !onRight.contains(it.second) }

            for (op in toProcess) {
                pMap[op.output] = op.doOp(pMap[op.first]!!, pMap[op.second]!!)
            }
            notProcessed = notProcessed.filter { !toProcess.contains(it) }
        }
        return pMap.filter { it.key.startsWith("z") }.toSortedMap()
            .values.reversed().map { if (it) 1 else 0 }.joinToString("").toLong(2)
    }

    private data class Part(val type: Char, val num: Int, var value: Boolean, val name: String)
    private data class Op(val first: String, val second: String, val output: String, val op: String) {
        fun hasInput(x: String) = first == x || second == x
        fun replace(map: Map<String, String>) = Op(r(first, map), r(second, map), r(output, map), op)
        fun doOp(x: Boolean, y: Boolean) = when (op) {
            "XOR" -> x xor y
            "AND" -> x && y
            "OR" -> x || y
            else -> throw IllegalArgumentException("Invalid op: $op")
        }

        private fun r(a: String, map: Map<String, String>) = if (map.containsKey(a)) map[a]!! else a

        override fun toString() =
            "${first.padStart(8, ' ')} $op ${second.padStart(8, ' ')} -> ${output.padStart(8, ' ')}"
    }
}