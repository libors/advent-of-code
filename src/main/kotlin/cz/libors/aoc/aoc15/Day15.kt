package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.findInts
import cz.libors.util.readToLines

@Day("Science for Hungry People")
object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input15.txt").map {
            val nums = it.findInts()
            Ingredient(it.substringBefore(':'), intArrayOf(nums[0], nums[1], nums[2], nums[3], nums[4]))
        }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Ingredient>) = find(input, ::score)
    private fun task2(input: List<Ingredient>) = find(input, ::cal500Score)

    private fun find(ingredients: List<Ingredient>, scoreFn: (List<Ingredient>, IntArray) -> Long): Long {
        val spoons = IntArray(ingredients.size)
        var max = 0L

        fun search(ing: Int, remain: Int) {
            if (ing == ingredients.size - 1) {
                spoons[ing] = remain
                val score = scoreFn(ingredients, spoons)
                if (score > max) max = score
            } else {
                for (i in 0 .. remain) {
                    spoons[ing] = i
                    search(ing + 1, remain - i)
                }
            }
        }

        search(0, 100)
        return max
    }

    private fun score(ingredients: List<Ingredient>, spoons: IntArray): Long {
        var mult = 1L
        for (propIdx in 0..3) {
            var propSum = 0
            for (ingIdx  in ingredients.indices) {
                propSum += ingredients[ingIdx].props[propIdx] * spoons[ingIdx]
            }
            if (propSum <= 0) return 0
            mult *= propSum
        }
        return mult
    }

    private fun cal500Score(ingredients: List<Ingredient>, spoons: IntArray): Long {
        var mult = 1L
        for (propIdx in 4 downTo 0) {
            var propSum = 0
            for (ingIdx  in ingredients.indices) {
                propSum += ingredients[ingIdx].props[propIdx] * spoons[ingIdx]
            }
            if (propIdx == 4) {
                if (propSum != 500) return 0
            } else {
                if (propSum <= 0) return 0
                mult *= propSum
            }
        }
        return mult
    }

    private class Ingredient(val name: String, val props: IntArray)
}