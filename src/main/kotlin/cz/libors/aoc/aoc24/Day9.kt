package cz.libors.aoc.aoc24

import cz.libors.util.Day
import cz.libors.util.readToText
import cz.libors.util.swap
import java.util.*

@Day("Disk Fragmenter")
object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input9.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun readToDisk(input: String) = input
        .flatMapIndexed { idx, c -> List(c.digitToInt()) { if (idx % 2 == 0) idx / 2 else -1 } }

    private fun readToBlocks(input: String): List<Block> {
        var position = 0
        val result = mutableListOf<Block>()
        for (i in input.indices) {
            val id = if (i % 2 == 0) i / 2 else -1
            val size = input[i].digitToInt()
            result.add(Block(id, position, size))
            position += size
        }
        return result
    }

    private fun task1(input: String): Long {
        val disk = readToDisk(input).toMutableList()
        var left = 0
        var right = disk.size - 1
        while (left < right) {
            while (left < disk.size && disk[left] != -1) left++
            while (right > 0 && disk[right] == -1) right--
            if (left < right) disk.swap(left++, right--)
        }
        return checksum(disk)
    }

    private fun task2(input: String): Long {
        val blocks = readToBlocks(input)
        val files = blocks.filter { it.id != -1 }
        val movedFiles = files.associateBy { it.id }.toMutableMap()
        val gaps = LinkedList(blocks.filter { it.id == -1 })
        for (file in files.reversed()) {
            val gapIterator = gaps.listIterator()
            while (gapIterator.hasNext()) {
                val gap = gapIterator.next()
                if (gap.pos > file.pos) {
                    break
                } else if (gap.size >= file.size) {
                    movedFiles[file.id] = Block(file.id, gap.pos, file.size)
                    gapIterator.remove()
                    if (gap.size > file.size) {
                        gapIterator.add(Block(-1, gap.pos + file.size, gap.size - file.size))
                    }
                    break
                }
            }
        }
        return movedFiles.values.sumOf { it.checksum() }
    }

    private fun checksum(disk: List<Int>): Long =
        disk.mapIndexed { idx, x -> if (x == -1) 0L else idx * x.toLong() }.sum()

    private data class Block(val id: Int, val pos: Int, val size: Int) {
        fun checksum() = (pos until pos + size).sumOf { it * id.toLong() }
    }
}