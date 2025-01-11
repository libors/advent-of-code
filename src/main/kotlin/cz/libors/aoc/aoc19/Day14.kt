package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.readToLines
import java.lang.RuntimeException
import kotlin.math.sign

@Day("Space Stoichiometry")
object Day14 {

    private const val TRILLION = 1000000000000

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input14.txt").map { parseReaction(it) }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(reactions: List<Reaction>) = findNeedOREs(outputToReactionMap(reactions), 1L)

    private fun task2(reactions: List<Reaction>): Long {
        val outputToReaction = outputToReactionMap(reactions)
        val oreForOneFuel = findNeedOREs(outputToReaction, 1)
        var i = TRILLION / oreForOneFuel
        while (findNeedOREs(outputToReaction, i) <= TRILLION) i += 1000 // get closer quickly
        while (findNeedOREs(outputToReaction, i) > TRILLION) i--
        return i
    }

    private fun outputToReactionMap(reactions: List<Reaction>) =
        reactions.groupBy { it.output.name }.mapValues { it.value.first() }.toMutableMap()

    private fun findNeedOREs(reactionsInput: MutableMap<String, Reaction>, fuelNeeded: Long): Long {
        var currentOutput = "FUEL"
        val amountsNeeded = mutableMapOf("FUEL" to fuelNeeded)
        val reactions = HashMap(reactionsInput)
        do {
            val r = reactions[currentOutput] ?: throw RuntimeException("No reaction with output $currentOutput")
            val needAtLeast = amountsNeeded[currentOutput]!!
            val outputAmount = r.output.amount
            val amount = if (outputAmount >= needAtLeast) 1 else
                needAtLeast / outputAmount + (needAtLeast % outputAmount).sign
            r.input.forEach { amountsNeeded[it.name] = (amountsNeeded[it.name] ?: 0) + it.amount * amount }
            reactions.remove(currentOutput)
            val onlyInOutput = takeNextOnlyInOutput(reactions.values)
            if (onlyInOutput != null) currentOutput = onlyInOutput
        } while (reactions.isNotEmpty())
        return amountsNeeded["ORE"]!!
    }

    private fun takeNextOnlyInOutput(reactions: Collection<Reaction>): String? {
        val x = reactions.map { it.output.name }
        if (x.isEmpty()) return null
        return x.filter { reactions.none { r -> r.input.map { it.name }.contains(it) } }[0]
    }

    private fun parseReaction(s: String): Reaction {
        val inputOutput = s.split("=>")
        val input = inputOutput[0].split(",").map { parseIngredient(it.trim()) }
        val output = parseIngredient(inputOutput[1].trim())
        return Reaction(input, output)
    }

    private fun parseIngredient(s: String): Ingredient = s.split(" ").let { Ingredient(it[1], it[0].toInt()) }

    private data class Ingredient(val name: String, val amount: Int)
    private data class Reaction(val input: List<Ingredient>, val output: Ingredient)
}