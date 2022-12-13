package com.behindmedia.adventofcode.year2022.day13

import com.behindmedia.adventofcode.common.*
import kotlin.math.min
import java.lang.StringBuilder

sealed class Packet: Comparable<Packet> {
    class PacketList(val packets: List<Packet>) : Packet() {
        override fun toString(): String {
            return "[" + packets.joinToString(",") + "]"
        }
    }
    class PacketLiteral(val value: Int) : Packet() {
        fun toList(): PacketList {
            return PacketList(packets = listOf(this))
        }

        override fun toString(): String {
            return value.toString()
        }
    }

    override fun compareTo(other: Packet): Int {
        return if (this is PacketLiteral && other is PacketLiteral) {
            this.value.compareTo(other.value)
        } else if (this is PacketLiteral) {
            this.toList().compareTo(other)
        } else if (other is PacketLiteral) {
            this.compareTo(other.toList())
        } else if (this is PacketList && other is PacketList) {
            var result: Int? = null
            val firstPackets = this.packets
            val secondPackets = other.packets
            for (i in 0 until min(firstPackets.size, secondPackets.size)) {
                val comparisonResult = firstPackets[i].compareTo(secondPackets[i])
                if (comparisonResult != 0) {
                    result = comparisonResult
                    break
                }
            }
            result ?: firstPackets.size.compareTo(secondPackets.size)
        } else {
            error("Should not reach this line")
        }
    }

    companion object {
        operator fun invoke(string: String): Packet {
            return if (string.startsWith("[")) {
                parsePacket(string, 1).first
            } else {
                error("Invalid packet string: $string")
            }
        }

        private fun parsePacket(string: String, startIndex: Int): Pair<Packet, Int> {
            val packets = mutableListOf<Packet>()
            val buffer = StringBuilder()
            fun append() {
                if (buffer.isNotEmpty()) {
                    val value = buffer.toString().toInt()
                    packets += PacketLiteral(value)
                    buffer.clear()
                }
            }
            var i = startIndex
            while (i < string.length) {
                when (val c = string[i++]) {
                    '[' -> {
                        // Start new packet
                        val (subPacket, nextIndex) = parsePacket(string, i)
                        packets += subPacket
                        i = nextIndex
                    }
                    ']' -> {
                        append()
                        // return current packet
                        return Pair(PacketList(packets), i)
                    }
                    ',' -> {
                        append()
                    }
                    ' ' -> {
                        // ignore
                    }
                    else -> {
                        buffer.append(c)
                    }
                }
            }
            error("Invalid packet string: $string")
        }
    }
}

fun main() {
    val packets = parseNonBlankLines("/2022/day13.txt") { line ->
        Packet(line)
    }

    part1(packets)
    part2(packets)
}

private fun part1(packets: List<Packet>) {
    var ans = 0
    for (i in packets.indices step 2) {
        val first = packets[i]
        val second = packets[i + 1]
        if (first <= second) {
            ans += (i / 2) + 1
        }
    }
    println(ans)
}

private fun part2(packets: List<Packet>) {
    val sortedPackets = packets.toMutableList()
    val packet1 = Packet("[[2]]")
    val packet2 = Packet("[[6]]")
    sortedPackets += packet1
    sortedPackets += packet2
    sortedPackets.sort()
    val ans = (1 + sortedPackets.indexOfFirst { it === packet1 }) * (1 + sortedPackets.indexOfFirst { it === packet2 })
    println(ans)
}