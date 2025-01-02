package cz.libors.aoc.aoc16

import cz.libors.util.*

@Day("Balance Bots")
object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input10.txt")
        val values = input.filter { it.startsWith("value") }
            .map { line -> line.findInts().let { Value(it[0], it[1]) } }
        val bots = input.filter { it.startsWith("bot") }
            .map { line ->
                val (id, o1, o2) = line.findInts()
                Bot(id,
                    Pair(o1, line.substringAfter("low to ").startsWith("bot")),
                    Pair(o2, line.substringAfter("high to ").startsWith("bot")))
            }
        println(task1(values, bots))
        println(task2(values, bots))
    }

    private fun task1(values: List<Value>, bots: List<Bot>) = analyze(values, bots).botInputs
        .filter { it.value.sorted() == listOf(17, 61) }.iterator().next().key

    private fun task2(values: List<Value>, bots: List<Bot>) = analyze(values, bots).outputs
        .filter { it.key in listOf(0, 1, 2) }
        .values.fold(1) { a, b -> a * b }

    private fun analyze (values: List<Value>, bots: List<Bot>): AnalysisResult {
        val botInputs = multiMap<Int, Int>()
        val processedBots = mutableSetOf<Int>()
        val outputs = mutableMapOf<Int, Int>()

        fun computeForBot(bot: Bot) {
            if (processedBots.contains(bot.id)) return
            values.filter { it.bot == bot.id }.forEach { botInputs.add(bot.id, it.value) }
            bots.filter { it.low == Pair(bot.id, true) || it.high == Pair(bot.id, true) }.forEach { computeForBot(it) }

            val inputs = botInputs[bot.id]!!.sorted()
            if (bot.low.second) botInputs.add(bot.low.first, inputs[0]) else outputs[bot.low.first] = inputs[0]
            if (bot.high.second) botInputs.add(bot.high.first, inputs[1]) else outputs[bot.high.first] = inputs[1]
            processedBots.add(bot.id)
        }

        bots.forEach { computeForBot(it) }
        return AnalysisResult(botInputs, outputs)
    }

    private data class AnalysisResult(val botInputs: Map<Int, List<Int>>, val outputs: Map<Int, Int>)
    private data class Value(val value: Int, val bot: Int)
    private data class Bot(val id: Int, val low: Pair<Int, Boolean>, val high: Pair<Int, Boolean>)
}
