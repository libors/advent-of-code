package cz.libors.aoc.aoc19

import cz.libors.util.*
import kotlin.math.abs
import kotlin.math.sign

@Day("The N-Body Problem")
object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToLines("input12.txt").map { line -> Moon(line.findInts().toPoint3()) }
        println(task1(input))
        println(task2(input))
    }

    private fun task1(moons: List<Moon>): Int {
        repeat(1000) { step(moons) }
        return energy(moons)
    }

    private fun task2(moons: List<Moon>): Long {
        val x = checkRepeat( moons.map { Moon1d(it.position.x, it.velocity.x) } )
        val y = checkRepeat( moons.map { Moon1d(it.position.y, it.velocity.y) } )
        val z = checkRepeat( moons.map { Moon1d(it.position.z, it.velocity.z) } )
        return lcm(x, lcm(y, z))
    }

    private fun step(moons: List<Moon>) {
        for (i in moons.indices)
            for (j in i + 1 until moons.size) {
                val m1 = moons[i]
                val m2 = moons[j]
                val dir = m1.directionTo(m2)
                m1.updateVelocity(-dir)
                m2.updateVelocity(dir)
            }
        moons.forEach { it.applyVelocity() }
    }

    private fun step1d(moons: List<Moon1d>) {
        for (i in moons.indices)
            for (j in i + 1 until moons.size) {
                val m1 = moons[i]
                val m2 = moons[j]
                val dir = m1.directionTo(m2)
                m1.updateVelocity(-dir)
                m2.updateVelocity(dir)
            }
        moons.forEach { it.applyVelocity() }
    }

    private fun energy(moons: List<Moon>) = moons.sumOf { it.potential() * it.kinetic() }

    private fun checkRepeat(moons: List<Moon1d>): Long {
        val initMoons = moons.map { it.copy() }
        var i = 0L
        do {
            i++
            step1d(moons)
        } while (moons != initMoons)
        return i
    }

    private data class Moon1d(var position: Int, var velocity: Int) {
        fun applyVelocity() {
            position += velocity
        }
        fun updateVelocity(dir: Int) {
            velocity += dir
        }
        fun directionTo(other: Moon1d) = (position - other.position).sign
    }

    private data class Moon(var position: Point3, var velocity: Vector3 = Vector3(0, 0, 0)) {
        fun applyVelocity() {
            position += velocity
        }
        fun updateVelocity(dir: Vector3) {
            velocity += dir
        }
        fun directionTo(other: Moon) = position.directionTo(other.position)
        fun potential() = position.absSum()
        fun kinetic() = velocity.absSum()
        override fun toString(): String {
            return "[p$position, v$velocity]"
        }
    }

    private fun Point3.absSum() = abs(x) + abs(y) + abs(z)
    private fun Point3.directionTo(other: Point3) = Vector3((x - other.x).sign, (y - other.y).sign, (z - other.z).sign)
    private fun Vector3.absSum() = abs(x) + abs(y) + abs(z)
}