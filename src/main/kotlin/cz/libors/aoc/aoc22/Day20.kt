package cz.libors.aoc.aoc22

import cz.libors.util.readToLines

object Day20 {

    private fun task1(input: List<Pair<Long, Int>>): Long {
        val list = input.toMutableList()
        mix(list)
        val idx0 = list.indexOfFirst { it.first == 0L }
        return listOf(1000L, 2000L, 3000L).sumOf { list[mod(idx0 + it, list.size)].first }
    }

    private fun task2(input: List<Pair<Long, Int>>): Long {
        val list = input
            .map { Pair(it.first * 811589153, it.second) }
            .toMutableList()
        for (i in 1..10) mix(list)

        val idx0 = list.indexOfFirst { it.first == 0L }
        return listOf(1000L, 2000L, 3000L).sumOf { list[mod(idx0 + it, list.size)].first }
    }

    private fun mix(list: MutableList<Pair<Long, Int>>) {
        for (num in list.indices) {
            val idx = list.indexOfFirst { it.second == num }
            val value = list[idx]
            val mod = mod(value.first, list.size - 1)
            if (value.first >= 0) {
                for (i in 0 until mod) {
                    list.swap(idx + i)
                }
            } else {
                val until = -(mod - (list.size - 1))
                for (i in 0 until until) {
                    list.swap(idx - 1 - i)
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val list = readToLines("input20.txt")
            .map { it.toLong() }
            .mapIndexed { idx, it -> Pair(it, idx) }

        println(task1(list))
        println(task2(list))
    }

    private inline fun <T> MutableList<T>.swap(idx: Int) {
        val first = mod(idx.toLong(), size)
        val second = mod(idx + 1L, size)
        val tmp = this[first]
        this[first] = this[second]
        this[second] = tmp
    }

    private inline fun mod(a: Long, clz: Int): Int =
        if (a >= 0) (a % clz).toInt() else (((a % clz) + clz) % clz).toInt()

}