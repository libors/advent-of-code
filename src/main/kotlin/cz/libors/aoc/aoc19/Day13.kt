package cz.libors.aoc.aoc19

import cz.libors.util.*
import java.util.*

@Day("Care Package")
object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input13.txt").findLongs().toLongArray()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(code: LongArray): Int = ArcadeCabinet(code).runCode()
        .getPlane().values.count { it == 2L }

    private fun task2(code: LongArray): Int {
        code[0] = 2L
        return ArcadeCabinet(code).runCode().score.toInt()
    }

    private class ArcadeCabinet(codeInput: LongArray) :
        ComputerImpl("arcade cabinet", codeInput, Input.value(), ArrayOutput()) {
        private val keyboard = Scanner(System.`in`)
        private val plane = mutableMapOf<Point, Long>()
        private var inputXPos = 0
        private var inputYPos = 0
        private var inputCnt = 0
        var score = 0L
        private val printer = PlanePrinter(mapOf(0L to ' ', 1L to '#', 2L to 'X', 3L to '-', 4L to 'o'))
        private var currentBallX = 0
        private var ballDirection = 0
        private var currentPaddleX = 0

        override fun runCode() = super.runCode() as ArcadeCabinet

        override fun writeOutput(value: Long) {
            when (inputCnt) {
                0 -> inputXPos = value.toInt()
                1 -> inputYPos = value.toInt()
                2 -> {
                    if (inputXPos == -1 && inputYPos == 0) {
                        score = value
                    } else {
                        plane[Point(inputXPos, inputYPos)] = value
                        if (value == 4L) {
                            ballDirection = if (currentBallX > inputXPos) -1 else 1
                            currentBallX = inputXPos
                        } else if (value == 3L) {
                            currentPaddleX = inputXPos
                        }
                    }
                }
            }
            inputCnt += 1
            inputCnt %= 3
        }

        override fun readInput() = computerInput()

        private fun computerInput() =
            if (ballDirection == 1) {
                if (currentBallX <= currentPaddleX) 0L else 1L
            } else {
                if (currentBallX > currentPaddleX) 0L else -1L
            }

        private fun playerInput(): Long {
            println(printer.print(plane))
            println(score)
            return when (keyboard.nextLine()[0]) {
                'j' -> -1L
                'k' -> 0L
                'l' -> 1L
                else -> throw RuntimeException("Expected j/k/l")
            }
        }

        fun getPlane(): Map<Point, Long> = plane
    }
}