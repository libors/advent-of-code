package cz.libors.aoc.aoc22

import cz.libors.util.findInts
import cz.libors.util.readToText
import cz.libors.util.splitByEmptyLine
import cz.libors.util.splitByNewLine

object Day11 {

    private fun task1(monkeys: List<Monkey>) = doRounds(monkeys, 20) { it / 3 }

    private fun task2(monkeys: List<Monkey>): Long {
        val product = monkeys.map { it.divisible }.reduce { a, b -> a * b }
        return doRounds(monkeys, 10000) { it % product }
    }

    private fun doRounds(monkeys: List<Monkey>, num: Int, factorOp: (Long) -> Long): Long {
        for (i in 1..num)
            round(monkeys, factorOp)
        return monkeys.map { it.inspections }
            .sorted()
            .takeLast(2)
            .fold(1L) { a, b -> a * b }
    }

    private fun round(monkeys: List<Monkey>, factorOp: (Long) -> Long) {
        for (monkey in monkeys) {
            for (item in monkey.items) {
                monkey.inspections++
                val factor = monkey.factor ?: item
                val newWorryLevel = factorOp(monkey.op(item, factor))
                val throwTo = if (newWorryLevel % monkey.divisible == 0L) monkey.trueMonkey else monkey.falseMonkey
                monkeys[throwTo].items.add(newWorryLevel)
            }
            monkey.items.clear()
        }
    }

    private fun toMonkey(data: List<String>): Monkey {
        val opString = with(data[2].split(' ')) { this[this.size - 2] }
        val factorString = with(data[2].split(' ')) { this[this.size - 1] }
        return Monkey(
            items = data[1].findInts().map { it.toLong() }.toMutableList(),
            op = if (opString == "*") { a, b -> a * b } else { a, b -> a + b },
            factor = if (factorString == "old") null else factorString.toLong(),
            divisible = data[3].findInts()[0].toLong(),
            trueMonkey = data[4].findInts()[0],
            falseMonkey = data[5].findInts()[0]
        )
    }

    private fun toMonkeys(input: List<String>): List<Monkey> =
        input.map { it.splitByNewLine().let(::toMonkey) }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input11.txt")
            .splitByEmptyLine()

        println(task1(toMonkeys(input)))
        println(task2(toMonkeys(input)))
    }

    data class Monkey(
        val items: MutableList<Long>,
        val op: (Long, Long) -> Long,
        val factor: Long?,
        val divisible: Long,
        val trueMonkey: Int,
        val falseMonkey: Int,
        var inspections: Int = 0
    )
}