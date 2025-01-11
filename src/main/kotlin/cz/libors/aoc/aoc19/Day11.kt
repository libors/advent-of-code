package cz.libors.aoc.aoc19

import cz.libors.util.*

@Day("Space Police")
object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input11.txt").findLongs().toLongArray()
        println(task1(input))
        println(task2(input))
    }

    private fun task1(code: LongArray) = Robot(code, 0L).runCode().getPlane().size

    private fun task2(code: LongArray): String {
        val robot = Robot(code, 1L).runCode()
        return PlanePrinter(mapOf(0L to ' ', 1L to '#')).print(robot.getPlane())
    }

    private class Robot(codeInput: LongArray, private val panelColor: Long) :
        ComputerImpl("robo", codeInput, Input.value(), ArrayOutput()) {

        private var waitForColorInput = true
        private val directions = listOf(Vector.UP, Vector.RIGHT, Vector.DOWN, Vector.LEFT)
        private val plane = mutableMapOf<Point, Long>()
        private var pos = Point(0, 0)
        private var facing = 0

        override fun runCode(): Robot = super.runCode() as Robot

        override fun writeOutput(value: Long) {
            if (waitForColorInput) {
                plane[pos] = value
                cdebug("!! paint $value on $pos")
            } else {
                if (value == 1L) {
                    facing = if (facing == 3) 0 else facing + 1
                    cdebug("!! turn right")
                } else {
                    facing = if (facing == 0) 3 else facing - 1
                    cdebug("!! turn left")
                }
                pos += directions[facing]
            }
            waitForColorInput = !waitForColorInput
        }

        override fun readInput() = plane[pos] ?: panelColor

        fun getPlane(): Map<Point, Long> = plane
    }
}