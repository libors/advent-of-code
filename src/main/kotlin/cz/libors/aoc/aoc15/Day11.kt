package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.readToText

@Day("Corporate Policy")
object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input11.txt")
        val first = task1(input)
        println(first)
        println(task1(first))
    }

    private fun task1(input: String): String {
        var s = input
        do {
            s = increase(s)
        } while (!check(s))
        return s
    }

    private fun check(s: String) = hasIncreasingLetters(s)
            && !s.contains('i') && !s.contains('o') && !s.contains('l')
            && hasPairs(s)

    private fun hasIncreasingLetters(s: String): Boolean {
        for (i in 0 until s.length - 2) {
            if (s[i] == s[i + 1] - 1 && s[i] == s[i + 2] - 2) return true
        }
        return false
    }

    private fun hasPairs(s: String): Boolean {
        var pairs = 0
        var i = 0
        while(i < s.length - 1) {
            if (s[i] == s[i + 1]) {
                if (pairs == 1) return true
                pairs++
                i++
            }
            i++
        }
        return false
    }

    private fun increase(s: String): String {
        val arr = s.toCharArray()

        fun inc(idx: Int) {
            if (arr[idx] == 'z') {
                arr[idx] = 'a'
                inc(idx - 1)
            } else {
                arr[idx] = arr[idx] + 1
            }
        }

        inc(arr.lastIndex)
        return arr.joinToString("")
    }
}