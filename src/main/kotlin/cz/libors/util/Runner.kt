package cz.libors.util

import cz.libors.aoc.aoc22.Day1
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Supplier

object Runner {

    data class Day(val num: Int, val time: Long, val lines: Int)

    @JvmStatic
    fun main(args: Array<String>) {
        val pckg = Day1.javaClass.packageName
        val days = ArrayList<Day>()
        for (i in 1..25) {
            val clazz = try {
                Class.forName("${pckg}.Day$i")
            } catch (e: Exception) {
                null
            }
            if (clazz != null) {
                println("\nDay $i\n------------------------------")
                val took =
                    measure { clazz.methods.first { it.name == "main" }.invoke(clazz.kotlin, arrayOf<String>()) }
                days.add(Day(i, took.second, getClassLines(clazz)))
            }
        }

        println("\n*****************************************")
        println("Total time spent: ${days.sumOf { x -> x.time } / 1000} seconds on ${days.size} days.")
        println("*****************************************")

        println("\nStatistics:\nday    ms  lines")
        days.forEach{
            d -> println("${d.num.toString().padStart(2)}  ${d.time.toString().padStart(5)}  "
                + (if (d.lines == 0) "" else d.lines.toString().padStart(5)))
        }
    }

    private fun getClassLines(clazz: Class<*>): Int {
        val location = clazz.getResource(clazz.getSimpleName() + ".class").path
        val srcLocation = location.replace("/build/classes/kotlin/main/", "/src/main/kotlin/")
            .replace(".class", ".kt")
        try {

            return Files.lines(Path.of(srcLocation)).count().toInt()
        } catch (e: Exception) {
            println(e)
            return 0
        }
    }

    private fun <T> measure(method: Supplier<T>): Pair<T, Long> {
        val start = System.currentTimeMillis()
        val result = method.get()
        val end = System.currentTimeMillis()
        return Pair(result, end - start)
    }

}