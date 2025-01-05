package cz.libors.aoc.aoc16

import cz.libors.util.*

@Day("One-Time Pad")
object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input14.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(salt: String) = find(salt, ::md5)
    private fun task2(salt: String) = find(salt, ::multiMd5)

    private fun find(salt: String, fn: (String) -> String): Int {
        val keys = mutableListOf<Int>()
        var i = 0
        var maxI = Int.MAX_VALUE
        val candidates = multiMap<Char, Int>()
        while (i < maxI) {
            val hash = fn("$salt$i")
            for (ch in getFives(hash)) {
                candidates[ch]?.forEach { tripletI ->
                    if (i - tripletI <= 1000) {
                        keys.add(tripletI)
                        if (keys.size == 64) maxI = i + 1000
                    }
                }
                candidates.remove(ch)
            }
            getTriplet(hash)?.let { candidates.add(it, i) }
            i++
        }
        return keys.sorted()[63]
    }

    private fun getTriplet(s: String): Char? {
        for (i in 0 until s.length - 2)
            if (s[i] == s[i + 1] && s[i] == s[i + 2]) return s[i]
        return null
    }

    private fun getFives(s: String): List<Char> {
        var i = 0
        val result = mutableListOf<Char>()
        while (i < s.length - 5) {
            val x = s[i]
            if (x == s[i + 1] && x == s[i + 2] && x == s[i + 3] && x == s[i + 4]) {
                if (!result.contains(x)) result.add(x)
                i += 4
            }
            i++
        }
        return result
    }

    private fun multiMd5(s: String): String {
        var x = s
        repeat(2017) { x = md5(x) }
        return x
    }
}