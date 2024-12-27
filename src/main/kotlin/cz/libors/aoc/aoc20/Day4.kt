package cz.libors.aoc.aoc20

import cz.libors.util.Day
import cz.libors.util.readToText
import cz.libors.util.splitByEmptyLine
import cz.libors.util.splitByNewLine

@Day("Passport Processing")
object Day4 {

    private val fields = mapOf<String, (String) -> Boolean>(
        "byr" to { (1920..2002).contains(it.toIntOrNull() ?: 0) },
        "iyr" to { (2010..2020).contains(it.toIntOrNull() ?: 0) },
        "eyr" to { (2020..2030).contains(it.toIntOrNull() ?: 0) },
        "hgt" to {
            val match = Regex("(\\d+)(cm|in)").matchEntire(it)?.groupValues
            if (match != null) {
                (if (match[2] == "cm") (150..193) else (59..76)).contains(match[1].toInt())
            } else false
        },
        "hcl" to { Regex("^#[0-9a-f]{6}$").matches(it) },
        "ecl" to { Regex("^(amb|blu|brn|gry|grn|hzl|oth)$").matches(it) },
        "pid" to { Regex("^[0-9]{9}$").matches(it) })

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input4.txt").splitByEmptyLine()
            .map { pp ->
                pp.splitByNewLine().flatMap { it.split(" ") }
                    .associate { item -> item.split(":").let { it[0] to it[1] } }
            }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<Map<String, String>>) = input.count { fields.keys.all { f -> it.containsKey(f) } }
    private fun task2(input: List<Map<String, String>>) =
        input.count { fields.all { (f, check) -> check(it[f] ?: "") } }
}