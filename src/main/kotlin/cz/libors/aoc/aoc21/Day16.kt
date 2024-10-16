package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.readToText
import kotlin.math.max
import kotlin.math.min

@Day("Packet Decoder")
object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input16.txt")
        println(versionSum(PacketParser(toBits(input)).parse()))
        println(compute(PacketParser(toBits(input)).parse()))
    }

    private fun compute(packet: Packet): Long {
        return when (packet.type) {
            0 -> packet.packets.fold(0) { acc, p -> acc + compute(p) }
            1 -> packet.packets.fold(1) { acc, p -> acc * compute(p) }
            2 -> packet.packets.fold(Long.MAX_VALUE) { acc, p -> min(acc, compute(p)) }
            3 -> packet.packets.fold(0) { acc, p -> max(acc, compute(p)) }
            4 -> packet.value
            5 -> if (compute(packet.packets[0]) > compute(packet.packets[1])) 1 else 0
            6 -> if (compute(packet.packets[0]) < compute(packet.packets[1])) 1 else 0
            7 -> if (compute(packet.packets[0]) == compute(packet.packets[1])) 1 else 0
            else -> throw IllegalArgumentException("Unknown packet type: ${packet.type}")
        }
    }

    private fun versionSum(packet: Packet): Int {
        return packet.version + packet.packets.fold(0) { acc, p -> acc + versionSum(p) }
    }

    private class PacketParser(val packets: String) {
        private var pos = 0

        private fun readLiteral(): Long {
            var literal = ""
            while (packets[pos] == '1') {
                literal += packets.substring(pos + 1, pos + 5)
                addPos(5, "literal chunk")
            }
            literal += packets.substring(pos + 1, pos + 5)
            addPos(5, "literal chunk")
            return literal.toLong(2)
        }

        private fun readPacket(): Packet {
            val version = packets.substring(pos, pos + 3).toInt(2)
            addPos(3, "version")
            val type = packets.substring(pos, pos + 3).toInt(2)
            addPos(3, "type")
            if (type == 4) {
                val literal = readLiteral()
                val packet = Packet(version, type, literal, listOf())
                return packet
            } else {
                val subpacketsLengthIndicator = if (packets[pos] == '0') 15 else 11
                addPos(1, "length type")
                val subPackets = mutableListOf<Packet>()
                if (subpacketsLengthIndicator == 15) {
                    val subpacketsLength = packets.substring(pos, pos + subpacketsLengthIndicator).toInt(2)
                    addPos(subpacketsLengthIndicator, "length num")
                    val readUntil = pos + subpacketsLength
                    while (pos < readUntil) {
                        subPackets.add(readPacket())
                    }
                } else {
                    val subpacketsNum = packets.substring(pos, pos + subpacketsLengthIndicator).toInt(2)
                    addPos(subpacketsLengthIndicator, "packets num")
                    for (i in 1..subpacketsNum) subPackets.add(readPacket())
                }
                val packet = Packet(version, type, 0, subPackets)
                return packet
            }
        }

        fun parse(): Packet {
            val result = readPacket()
            if (pos < packets.length)
                addPos(packets.length - pos, "end padding")
            return result
        }

        private fun addPos(i: Int, why: String) {
            pos += i
        }
    }

    private fun toBits(hex: String) = hex.toCharArray()
        .joinToString("") { it.digitToInt(16).toString(2).padStart(4, '0') }

    private data class Packet(val version: Int, val type: Int, val value: Long, val packets: List<Packet>)

}