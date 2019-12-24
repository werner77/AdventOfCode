package com.behindmedia.adventofcode2019

import java.util.*

class Day23 {

    data class Packet(val x: Long, val y: Long)

    private fun Packet?.toInput(): List<Long> {
        return if (this == null) {
            listOf(-1L)
        } else {
            listOf(this.x, this.y)
        }
    }

    private class PacketQueue {
        private val map = mutableMapOf<Long, LinkedList<Packet>>()

        fun isEmpty(): Boolean {
            for (list in map.values) {
                if (!list.isEmpty()) {
                    return false
                }
            }
            return true
        }

        fun putPacket(address: Long, packet: Packet) {
            map.getOrPut(address) { LinkedList() }.add(packet)
        }

        fun popFirstPacket(address: Long): Packet? {
            return map[address]?.popFirst()
        }
    }

    fun processNetwork(program: String, onDelivery: (Long, Packet, Boolean) -> Boolean): Packet {

        val computerCount = 50
        val computers = Array(computerCount) {
            val computer = Computer(program)
            computer.process(it.toLong())
            computer
        }
        val packetQueue = PacketQueue()
        var natPacket: Packet? = null
        var emptyQueueCount = 0

        network@while(true) {
            // Process outputs
            if (packetQueue.isEmpty()) {
                emptyQueueCount++
            } else {
                emptyQueueCount = 0
            }

            if (emptyQueueCount == 5 && natPacket != null) {
                packetQueue.putPacket(0, natPacket)
                if (onDelivery(0, natPacket, true)) {
                    return natPacket
                }
                natPacket = null
            }

            for (i in computers.indices) {

                val result = computers[i].process(packetQueue.popFirstPacket(i.toLong()).toInput())

                assert(result.outputs.size % 3 == 0)
                for (j in result.outputs.indices step 3) {
                    val address = result.outputs[j]
                    val packet = Packet(result.outputs[j + 1], result.outputs[j + 2])
                    if (address == 255L) {
                        natPacket = packet
                    } else {
                        packetQueue.putPacket(address, packet)
                        if (onDelivery(address, packet, false)) {
                            return packet
                        }
                    }
                }
            }
        }
    }
}