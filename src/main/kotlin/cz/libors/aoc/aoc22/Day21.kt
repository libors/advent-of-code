package cz.libors.aoc.aoc22

import cz.libors.util.BinOperation
import cz.libors.util.readToLines

object Day21 {

    private fun task1(monkeys: Map<String, Monkey>) = monkeys["root"]!!.evaluate(monkeys)

    private fun task2(monkeys: Map<String, Monkey>): Long {
        val m = monkeys.toMutableMap()
        val root = monkeys["root"]!!
        m["root"] = Monkey(name = root.name, m1 = root.m1, m2 = root.m2, op = ('='))
        m["humn"] = Monkey(name = "humn")
        val result = m["root"]!!.evaluateVar(m, 0L)
        return result.x!!
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input21.txt")
            .map {
                val p = it.split(" ")
                val name = p[0].substringBefore(":")
                if (p.size == 4)
                    Monkey(name = name, m1 = p[1], m2 = p[3], op = p[2][0])
                else
                    Monkey(name = name, num = p[1].toLong())
            }.associateBy { it.name }

        println(task1(input))
        println(task2(input))
    }

    private fun Monkey.evaluateVar(monkeys: Map<String, Monkey>, desired: Long?): Result {
        if (num != null) {
            return Result(num, null)
        }
        if (m1 == null) {
            return Result(desired, desired) // finally found human value
        }
        val left = monkeys[m1]!!.evaluateVar(monkeys, null)
        val right = monkeys[m2]!!.evaluateVar(monkeys, null)
        if (left.x != null || right.x != null) {
            return Result(BinOperation(op!!)(left.v!!, right.v!!), left.x ?: right.x)
        }
        if (left.v != null && right.v != null) {
            return Result(BinOperation(op!!)(left.v, right.v), left.x ?: right.x)
        }
        if (desired == null) {
            return Result(null, null) // should set up desired and compute again
        }

        val newDesired = when (op) {
            '=' -> left.v ?: right.v
            '-' -> if (left.v == null) desired + right.v!! else left.v - desired
            '+' -> if (left.v == null) desired - right.v!! else desired - left.v
            '*' -> if (left.v == null) desired / right.v!! else desired / left.v
            '/' -> if (left.v == null) desired * right.v!! else left.v / desired
            else -> throw IllegalArgumentException()
        }

        val resolved = if (left.v == null) {
            monkeys[m1]!!.evaluateVar(monkeys, newDesired)
        } else {
            monkeys[m2]!!.evaluateVar(monkeys, newDesired)
        }
        val oper = BinOperation(op)
        val value = if (left.v == null) oper(resolved.v!!, right.v!!) else oper(left.v, resolved.v!!)
        return Result(value, resolved.x)
    }

    data class Result(val v: Long?, val x: Long?)

    private fun Monkey.evaluate(monkeys: Map<String, Monkey>): Long =
        num ?: BinOperation(op!!)(monkeys[m1]!!.evaluate(monkeys), monkeys[m2]!!.evaluate(monkeys))

    private data class Monkey(
        val name: String,
        val num: Long? = null,
        val m1: String? = null,
        val m2: String? = null,
        val op: Char? = null
    )

}