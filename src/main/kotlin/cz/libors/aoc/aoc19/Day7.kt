package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.findLongs
import cz.libors.util.permutations
import cz.libors.util.readToText
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

@Day("Amplification Circuit")
object Day7 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input7.txt").findLongs().toLongArray()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(code: LongArray): Long {
        val perms = permutations(listOf(0L, 1, 2, 3, 4).toTypedArray())
        return perms.maxOf { runSetup(code, it.toLongArray()) }
    }

    private fun task2(code: LongArray): Long {
        val executor = Executors.newFixedThreadPool(5)
        val perms = permutations(listOf(5L, 6, 7, 8, 9).toTypedArray())
        return perms.maxOf { runAsyncSetup(executor, code, it.toLongArray()) }.also { executor.shutdown() }
    }

    private fun runSetup(code: LongArray, setup: LongArray): Long {
        var inputValue = 0L
        for (i in 0..4) {
            inputValue = Computer.create("c$i", code, Input.value(setup[i], inputValue))
                .runCode()
                .getLastOutput()
        }
        return inputValue
    }

    private fun runAsyncSetup(executor: ExecutorService, code: LongArray, setup: LongArray): Long {
        val channels = Array(5) { LinkedBlockingQueue<Long>(10) }
        (0..4).forEach { channels[it].put(setup[it]) }
        channels[0].put(0)

        val tasks = (0..4).map {
            executor.submit {
                Computer.create("c$it", code, Input.queue(channels[it]), Output.queue(channels[(it + 1) % 5])).runCode()
            }
        }
        tasks.forEach { it.get() }
        return channels[0].poll()
    }
}