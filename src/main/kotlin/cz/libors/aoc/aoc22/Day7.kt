package cz.libors.aoc.aoc22

import cz.libors.util.readToText
import cz.libors.util.splitByNewLine

object Day7 {

    private fun runCommands(input: List<List<String>>): List<FSItem> {
        val fs = FileSystem()
        for (command in input) {
            if (command[0].startsWith("cd")) {
                fs.goto(command[0].substringAfter("cd "))
            } else if (command[0] == "ls") {
                fs.updateDir(command.subList(1, command.size).map { it.split(' ') })
            }
        }
        return fs.getAll()
    }

    private fun task1(input: List<List<String>>) = runCommands(input)
        .filter { it.isDir() }
        .map { it.subSize() }
        .filter { it < 100000 }
        .sum()

    private fun task2(input: List<List<String>>): Int {
        val dirs = runCommands(input)
            .filter { it.isDir() }
            .map { it.subSize() }
        val used = dirs.maxOrNull()!!
        val free = 70_000_000 - used
        return dirs.filter { free + it > 30_000_000 }.minOrNull()!!
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input7.txt")
            .split('$')
            .map { group -> group.splitByNewLine().map { it.trim() }.filter { it.isNotEmpty() } }
            .filter { it.isNotEmpty() }
        println(task1(input))
        println(task2(input))
    }

    class FileSystem {
        private val root = FSItem("/", 0)
        private var current = root
        private val path = mutableListOf<FSItem>()

        fun goto(name: String) {
            if (name == "/") {
                path.clear()
                current = root
            } else if (name == "..") {
                current = path.removeLast()
            } else {
                path.add(current)
                current = current.goto(name)
            }
        }

        fun updateDir(what: List<List<String>>) = what.forEach { current.add(it) }

        fun getAll(): List<FSItem> = root.list()

    }

    data class FSItem(val name: String, val size: Int) {
        private val subs = mutableMapOf<String, FSItem>()
        fun goto(name: String) = subs.getOrPut(name) { FSItem(name, 0) }
        fun add(what: List<String>) {
            if (what[0] == "dir")
                subs.putIfAbsent(what[1], FSItem(what[1], 0))
            else
                subs[what[1]] = FSItem(what[1], what[0].toInt())
        }

        fun isDir() = size == 0
        fun subSize(): Int = if (size != 0) size else subs.values.sumOf { it.subSize() }
        fun list(): List<FSItem> = subs.values.flatMap { it.list() } + this
    }
}