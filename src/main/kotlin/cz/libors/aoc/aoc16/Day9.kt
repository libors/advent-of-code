package cz.libors.aoc.aoc16

import cz.libors.util.Composite
import cz.libors.util.Day
import cz.libors.util.readToText

@Day("Explosives in Cyberspace")
object Day9 {
    private val regex = Regex("\\((\\d+)x(\\d+)\\)")

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input9.txt")
        println(task1(input))
        println(task2(input))
    }

    private fun task1(input: String): Int {
        val definitions = regex.findAll(input)
        var pos = 0
        var len = 0
        for (def in definitions) {
            if (def.range.start < pos) continue
            len += def.range.start - pos - 1
            val (exp, chars, repeat) = def.groupValues
            len += chars.toInt() * repeat.toInt()
            pos = def.range.endInclusive + chars.toInt()
        }
        len += input.length - pos
        return len
    }

    private fun task2(s: String): Long {
        val definitions = regex.findAll(s).map { toDef(it) }.toList()
        val tree = definitionsToTree(s, definitions)
        return getSize(tree)
    }

    private fun getSize(def: Composite<DecompressDef>): Long {
        var result = def.item.size.toLong()
        for (sub in def.children) {
            result = result - sub.item.defLength() + getSize(sub)
        }
        result *= def.item.repeat
        return result
    }

    private fun toDef(it: MatchResult): DecompressDef {
        val v = it.groupValues
        val start = it.range.endInclusive + 1
        return DecompressDef(it.range.first, v[1].toInt(), v[2].toInt(), start, start + v[1].toInt() - 1)
    }

    private fun definitionsToTree(s: String, definitions: List<DecompressDef>): Composite<DecompressDef> {
        val highLevel = Composite(DecompressDef(0, s.length, 1, 0, s.length - 1))
        val hierarchy = mutableListOf(highLevel)
        for (def in definitions) {
            while (!hierarchy.last().item.contains(def)) hierarchy.removeLast()
            val c = Composite(def)
            hierarchy.last().add(c)
            hierarchy.add(c)
        }
        return highLevel
    }

    private data class DecompressDef(val defStart: Int, val size: Int, val repeat: Int, val start: Int, val end: Int) {
        fun contains(e: DecompressDef) = e.start > start && e.end <= end
        fun defLength() = end - defStart + 1
    }
}