package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.read
import org.junit.Test
import kotlin.test.assertEquals

class Day23Test {

    @Test
    fun puzzle1() {
        val program = read("/day23.txt")
        val day23 = Day23()

        val packet = day23.processNetwork(program) { _, _, isNat ->
            isNat
        }

        println(packet.y)
        assertEquals(21160, packet.y)
    }

    @Test
    fun puzzle2() {
        val program = read("/day23.txt")
        val day23 = Day23()

        var lastNatPacket: Day23.Packet? = null

        val packet = day23.processNetwork(program) { _, packet, isNat ->
            var found = false
            if (isNat) {
                if (packet.y == lastNatPacket?.y) {
                    found = true
                }
                lastNatPacket = packet
            }
            found
        }

        println(packet.y)
        assertEquals(14327, packet.y)
    }
}