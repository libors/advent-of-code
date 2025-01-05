package cz.libors.aoc.aoc15

import cz.libors.util.Day
import cz.libors.util.readToText
import java.security.MessageDigest

@Day("The Ideal Stocking Stuffer")
object Day4 {

    private val md = MessageDigest.getInstance("MD5")

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input4.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String) = findNum(input, "00000")
    private fun task2(input: String) = findNum(input, "000000")

    private fun findNum(input: String, prefix: String): Int {
        var i = 0
        while (true) {
            if ((input + i.toString()).md5().startsWith(prefix)) return i
            i++
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun String.md5() = md.digest(this.toByteArray()).toHexString()
}