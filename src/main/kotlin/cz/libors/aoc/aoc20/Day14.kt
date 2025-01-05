package cz.libors.aoc.aoc20

import cz.libors.util.*

@Day("Docking Data")
object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input14.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>): Long {
        val memory = mutableMapOf<Long, Long>()
        var mask = ""
        for (line in input) {
            if (line.startsWith("mask")) {
                mask = line.substringAfterLast(" ")
            } else {
                val (addr, value) = line.findLongs()
                memory[addr] = applyValueMask(value, mask)
            }
        }
        return memory.values.sum()
    }

    private fun task2(input: List<String>): Long {
        val memory = mutableMapOf<Long, Long>()
        var mask = ""
        for (line in input) {
            if (line.startsWith("mask")) {
                mask = line.substringAfterLast(" ")
            } else {
                val (addr, value) = line.findLongs()
                val addresses = applyMemoryMask(addr, mask)
                for (a in addresses) {
                    memory[a] = value
                }
            }
        }
        return memory.values.sum()
    }

    private fun applyMemoryMask(value: Long, mask: String): List<Long> {
        val result = mutableListOf<Long>()
        val floating = mutableListOf<Int>()
        var num = value
        for (i in mask.indices) {
            val pos = 35 - i
            when (mask[i]) {
                '1' -> num = num.set(pos)
                'X' -> floating.add(pos)
            }
        }

        fun float(num: Long, idx: Int) {
            if (idx == floating.size) {
                result.add(num)
            } else {
                float(num.set(floating[idx]), idx + 1)
                float(num.unset(floating[idx]), idx + 1)
            }
        }

        float(num, 0)
        return result
    }

    private fun applyValueMask(value: Long, mask: String): Long {
        var result = value
        for (i in mask.indices) {
            val pos = 35 - i
            when (mask[i]) {
                '0' -> result = result.unset(pos)
                '1' -> result = result.set(pos)
            }
        }
        return result
    }
}