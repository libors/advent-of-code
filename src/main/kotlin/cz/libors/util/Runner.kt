package cz.libors.util

import java.lang.reflect.InvocationTargetException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Supplier

object Runner {

    private val years = listOf("18", "20", "21", "22", "23", "24")

    private data class DayRecord(val year: Int, val num: Int, val time: Long, val lines: Int, val notImplemented: Boolean)

    @JvmStatic
    fun main(args: Array<String>) {
        val year = menu()
        val summary = mutableListOf<List<DayRecord>>()
        if (year.isBlank()) {
            years.forEach { summary.add(runForYear("cz.libors.aoc.aoc$it")) }
        } else {
            summary.add(runForYear("cz.libors.aoc.aoc$year"))
        }
        println("\n*****************************************")
        println("*                SUMMARY                *")
        println("*****************************************")
        summary.forEach { event ->
            println("20${event[0].year} : ${event.size} days solved in " +
                    "${event.sumOf { it.time } / 1000F} seconds, ${event.sumOf { it.lines }} LOC.")
        }
    }

    private fun runForYear(pckg: String): List<DayRecord> {
        val dayClasses = getClasses(pckg)
        val year = pckg.findInts()[0]
        require(!downloadMissingInputs(year, dayClasses)) {
            "Some inputs were downloaded to classpath resources. Run again."
        }

        val dayRecords = ArrayList<DayRecord>()
        println("\n*****************************************")
        println("*                20$year                   *")
        println("*****************************************")
        for (clazz in dayClasses) {
            val day = clazz.simpleName.findInts()[0]
            val dayAnnotation = clazz.getAnnotation(Day::class.java)
            val dayName = if (dayAnnotation != null) ": " + dayAnnotation.name else "";
            println("\nDay $day (20$year) $dayName")
            val took = measure {
                try {
                    clazz.methods.first { it.name == "main" }.invoke(clazz.kotlin, arrayOf<String>())
                    false
                } catch (e: InvocationTargetException) {
                    if (e.cause != null && e.cause!!::class == NotImplementedError::class) {
                        println(e.cause!!.message)
                        true
                    } else throw e
                }
            }
            dayRecords.add(DayRecord(year, day, took.second, getClassLines(clazz), took.first))
        }

        printStatistics(dayRecords)
        return dayRecords
    }

    private fun menu(): String {
        println("Pick year to run:")
        println(years.joinToString(" / "))
        println("ENTER -> all years")
        print("> ")
        val option = readln()
        require (option.isEmpty() || years.contains(option.trim())) { "$option year is not supported" }
        return option
    }

    private fun printStatistics(dayRecords: ArrayList<DayRecord>) {
        println("\nStatistics (20${dayRecords[0].year}):\nday    ms  lines")
        dayRecords.forEach { d ->
            println(
                "${d.num.toString().padStart(2)}  ${d.time.toString().padStart(5)}  "
                        + (if (d.lines == 0) "" else d.lines.toString().padStart(5))
            )
        }

        val notFinished = dayRecords.filter { it.notImplemented }.map { it.num }
        if (notFinished.isNotEmpty()) {
            println("\nNot finished days: ${notFinished.joinToString(", ")}\n")
        }
    }

    private fun downloadMissingInputs(year: Int, dayClasses: List<Class<*>>) = dayClasses
        .map { downloadInput(it, year) }
        .any { it }

    private fun getClasses(pckg: String) = (1..25).mapNotNull {
        try {
            Class.forName("${pckg}.Day$it")
        } catch (e: Exception) {
            null
        }
    }

    private fun getClassLines(clazz: Class<*>): Int {
        val location = clazz.getResource(clazz.getSimpleName() + ".class")!!.path
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

    private fun downloadInput(dayClass: Class<*>, year: Int): Boolean {
        val day = dayClass.simpleName.findInts()[0]
        val projectLocation =
            dayClass.getResource(dayClass.getSimpleName() + ".class")!!.path.substringBefore("build/classes")
        val resourceLocation = Path.of(projectLocation + "src/main/resources/$year/input${day}.txt")

        if (Files.exists(resourceLocation)) return false
        println("NOT EXIST $resourceLocation")

        val url = "https://adventofcode.com/20$year/day/$day/input"
        val sessionCookie = System.getProperty("aoc-cookie")
        require(sessionCookie != null) {
            "Add AOC session cookie value from web browser to aoc-cookie system property to authenticate to download inputs."
        }

        val con: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        con.setRequestMethod("GET")
        con.addRequestProperty("Cookie", "session=$sessionCookie")

        var inputContent = con.inputStream.bufferedReader().use { it.readText().lines() }
        if (inputContent[inputContent.size - 1].isEmpty()) {
            inputContent = inputContent.subList(0, inputContent.size - 1)
        }

        Files.write(resourceLocation, inputContent.joinToString("\n").toByteArray())
        println("Input $resourceLocation has been downloaded")
        return true
    }

}