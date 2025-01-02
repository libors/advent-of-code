package cz.libors.aoc.aoc16

import cz.libors.util.Day
import cz.libors.util.readToLines

@Day("Internet Protocol Version 7")
object Day7 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input7.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>) = input.count { supportsTls(it) }
    private fun task2(input: List<String>) = input.count { supportsSsl(it) }

    private fun supportsTls(s: String): Boolean {
        var hasPair = false
        var inSquare = false
        for (i in 0..s.length - 4) {
            when (s[i]) {
                '[' -> inSquare = true
                ']' -> inSquare = false
                else -> if (s[i] != s[i + 1] && s[i + 1] == s[i + 2] && s[i] == s[i + 3] && s[i + 1] != ']' && s[i + 1] != '[') {
                    if (inSquare) return false else hasPair = true
                }
            }
        }
        return hasPair
    }

    private fun supportsSsl(s: String): Boolean {
        var inSquare = false
        val superAbas = mutableSetOf<String>()
        val hyperAbas = mutableSetOf<String>()
        for (i in 0..s.length - 3) {
            when (s[i]) {
                '[' -> inSquare = true
                ']' -> inSquare = false
                else -> if (s[i] != s[i + 1] && s[i] == s[i + 2] && s[i + 1] != ']' && s[i + 1] != '[') {
                    if (inSquare) {
                        val aba = "${s[i+1]}${s[i]}"
                        if (superAbas.contains(aba)) return true else hyperAbas.add(aba)
                    } else {
                        val aba = "${s[i]}${s[i+1]}"
                        if (hyperAbas.contains(aba)) return true else superAbas.add(aba)
                    }
                }
            }
        }
        return false
    }
}