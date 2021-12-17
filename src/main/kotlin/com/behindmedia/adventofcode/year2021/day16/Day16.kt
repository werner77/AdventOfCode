package com.behindmedia.adventofcode.year2021.day16

import com.behindmedia.adventofcode.common.max
import com.behindmedia.adventofcode.common.min
import com.behindmedia.adventofcode.common.parse
import org.apache.commons.compress.utils.BitInputStream
import java.io.ByteArrayInputStream
import java.nio.ByteOrder

class BitStream(private val bytes: ByteArray) {
    private val inputStream = BitInputStream(ByteArrayInputStream(bytes), ByteOrder.BIG_ENDIAN)

    var pos: Int = 0
        private set

    val hasMore: Boolean
        get() = pos < bytes.size * 8

    fun readBits(count: Int): Long {
        val bits = inputStream.readBits(count)
        if (bits == -1L) error("EOF")
        return bits.also { pos += count }
    }

    fun readUntilNextByte(): Long {
        val b = pos % 8
        return if (b == 0) {
            0L
        } else {
            readBits(8 - b)
        }
    }
}

private fun String.decodeHex(): ByteArray {
    require(length % 2 == 0) {
        "Must have an even length"
    }
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

private typealias PacketFunction = (List<Long>) -> Long

private data class Packet(val version: Long, val id: Long, val level: Int, val result: Long)

private fun Iterable<Long>.product(): Long {
    return this.fold(1L) { product, value -> product * value }
}

private fun <E> List<E>.second() : E {
    return getOrNull(1) ?: throw NoSuchElementException("Could not find any element at index 1")
}

private fun readPacket(bitStream: BitStream, level: Int = 0, packetHandler: (Packet) -> Unit): Pair<Int, Long> {
    val packetResult: Long
    val startPos = bitStream.pos
    val version = bitStream.readBits(3)
    val id = bitStream.readBits(3)
    when (id) {
        4L -> {
            // Read literal
            var literal = 0L
            while (true) {
                val next = bitStream.readBits(5)
                literal = (literal shl 4) or (next and 0x0FL)
                if ((next and 0x10L) == 0L) {
                    break
                }
            }
            packetResult = literal
        }
        else -> {
            val function: PacketFunction = when (id) {
                0L -> { it -> it.sum() }
                1L -> { it -> it.product() }
                2L -> { it -> it.min() }
                3L -> { it -> it.max() }
                5L -> { it -> if (it.first() > it.second()) 1 else 0 }
                6L -> { it -> if (it.first() < it.second()) 1 else 0 }
                7L -> { it -> if (it.first() == it.second()) 1 else 0 }
                else -> error("Unrecognized packet id: $id")
            }
            val type = bitStream.readBits(1)
            val results = mutableListOf<Long>()
            if (type == 0L) {
                val subPacketLength = bitStream.readBits(15).toInt()
                var totalReadCount = 0
                while (totalReadCount < subPacketLength) {
                    val (readCount, result) = readPacket(bitStream, level + 1, packetHandler)
                    results += result
                    totalReadCount += readCount
                }
                require(totalReadCount == subPacketLength) {
                    "Invalid read count: $totalReadCount, expected: $subPacketLength"
                }
            } else {
                val subPacketCount = bitStream.readBits(11).toInt()
                repeat(subPacketCount) {
                    val (_, result) = readPacket(bitStream, level + 1, packetHandler)
                    results += result
                }
            }
            packetResult = function(results)
        }
    }
    if (level == 0) {
        bitStream.readUntilNextByte()
        require(!bitStream.hasMore) {
            "Expected no more bits to read"
        }
    }
    packetHandler.invoke(Packet(version, id, level, packetResult))
    return Pair(bitStream.pos - startPos, packetResult)
}

fun main() {
    val data = parse("/2021/day16.txt") { line ->
        line.trim().decodeHex()
    }
    val bitStream = BitStream(data)
    var versionSum = 0L
    val result = readPacket(bitStream) {
        versionSum += it.version
    }

    // Part 1
    println(versionSum)

    // Part 2
    println(result.second)
}