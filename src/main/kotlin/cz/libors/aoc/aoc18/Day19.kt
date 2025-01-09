package cz.libors.aoc.aoc18

import cz.libors.aoc.aoc18.Day16.opcodes
import cz.libors.util.findInts
import cz.libors.util.readToLines

object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input19.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: List<String>) = createComputer(input).run()[0]
    private fun task2(input: List<String>): Unit = TODO("reverse engineer")

    private fun createComputer(input: List<String>): Computer {
        val iReg = input[0].findInts()[0]
        val instructions = input.drop(1).map {
            val nums = it.findInts()
            Instruction(it.substringBefore(' '), nums[0], nums[1], nums[2])
        }
        return Computer(instructions, iReg)
    }

    private class Computer(val program: List<Instruction>, val iReg: Int) {
        val regs = mutableListOf(0, 0, 0, 0, 0, 0)
        val ops = opcodes.associateBy { it.name }
        var cnt = 0
        fun run(): List<Int> {
            while (regs[iReg] < program.size && regs[iReg] >= 0) {
                val i = program[regs[iReg]]
                ops[i.name]!!.exec(i.a, i.b, i.c, regs)
                regs[iReg]++
                if (++cnt % 100000000 == 0) println("$cnt $regs")
            }
            regs[iReg]--

            return regs
        }
    }

    private data class Instruction(val name: String, val a: Int, val b: Int, val c: Int)
}