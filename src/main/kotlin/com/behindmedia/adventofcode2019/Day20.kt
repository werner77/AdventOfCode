package com.behindmedia.adventofcode2019

class Day20 {

    data class Portal(val identifier: String, val outside: Boolean, var connectedNode: Node? = null)

    data class LeveledCoordinate(val coordinate: Coordinate, val level: Int)

    data class Node(val coordinate: Coordinate, val portal: Portal?) {

        fun isReachable(level: Int, recursiveMode: Boolean): Boolean {
            assert(level >= 0)
            if (portal != null) {
                if (portal.connectedNode == null) {
                    // No connected portal, which means it is a global entry or exit, which is reachable only at level 0
                    return level == 0
                } else if (portal.outside && recursiveMode) {
                    // Outside portal, only reachable when level > 0
                    return level > 0
                }
            }
            return true
        }

        fun neighbours(level: Int, recursiveMode: Boolean): List<LeveledCoordinate> {
            val result = coordinate.neighbours.map { LeveledCoordinate(it, level) }.toMutableList()
            if (isReachable(level, recursiveMode)) {
                portal?.let { portal ->
                    portal.connectedNode?.let { node ->
                        if (portal.outside) {
                            // decrease level
                            result.add(LeveledCoordinate(node.coordinate, if (recursiveMode) level - 1 else level))
                        } else {
                            // increase level
                            result.add(LeveledCoordinate(node.coordinate, if (recursiveMode) level + 1 else level))
                        }
                    }
                }
            }
            return result
        }
    }

    private fun printMap(map: Map<Coordinate, Char>) {
        val range = map.keys.range()
        for (coordinate in range) {
            val c: Char = map[coordinate] ?: ' '
            print("$c")
            if (coordinate.x == range.endInclusive.x) {
                println()
            }
        }
    }

    private fun parseMap(input: String): Map<Coordinate, Node> {

        // First parse the input into a straight map of Coordinate -> Char
        val map = input.split('\n').foldIndexed(mutableMapOf<Coordinate, Char>()) { y, map, line ->
            var x = 0
            for (c in line) {
                if (c != ' ') {
                    val coordinate = Coordinate(x, y)
                    map[coordinate] = c
                }
                x++
            }
            map
        }

        // Lookup map for the portals, key is identifier and value is a list of nodes corresponding with this identifier
        val portalMap = mutableMapOf<String?, MutableList<Node>>()

        // The map of nodes to return
        val nodeMap = mutableMapOf<Coordinate, Node>()

        val range = map.keys.range()
        val maxCoordinate = range.endInclusive

        for (coordinate in range) {
            val c1 = map[coordinate]

            // Only consider open nodes
            if (c1 == '.') {
                var portal: Portal? = null

                for (neighbour in coordinate.neighbours) {
                    val c2 = map[neighbour] ?: ' '
                    if ((c2 - 'A') in 0..25) {
                        // Consider the next coordinate as well
                        val direction = coordinate.vector(neighbour)
                        val nextNeighbour = neighbour.offset(direction)

                        val c3 = map[nextNeighbour] ?: throw IllegalStateException("Expected another letter to be present, portals should have two-lettered identifiers")

                        // Make a string of these two characters: that's the portal identifier
                        val outside = coordinate.x == 2 || coordinate.y == 2 ||
                                    coordinate.x == maxCoordinate.x - 2 || coordinate.y == maxCoordinate.y - 2

                        portal = if (direction == Coordinate.left || direction == Coordinate.up) {
                            Portal("$c3$c2", outside)
                        } else {
                            Portal("$c2$c3", outside)
                        }
                        break
                    }
                }

                val node = Node(coordinate, portal)
                portalMap.getOrPut(portal?.identifier) {
                    mutableListOf()
                }.add(node)

                nodeMap[coordinate] = node
            }
        }

        // Now connect the nodes with similar portals
        for (portals in portalMap.entries.filter { it.key != null }) {
            assert(portals.value.size in 1..2)
            if (portals.value.size == 2) {
                val node1 = portals.value[0]
                val node2 = portals.value[1]

                assert(node1.portal != null)
                assert(node2.portal != null)
                assert(node1.portal?.outside != node2.portal?.outside)

                node1.portal?.connectedNode = node2
                node2.portal?.connectedNode = node1
            }
        }

        return nodeMap
    }

    fun findMinimumPath(input: String, startIdentifier: String = "AA", endIdentifier: String = "ZZ", recursive: Boolean): Int {

        val map = parseMap(input)

        val start = map.values.find { it.portal?.identifier == startIdentifier } ?: throw IllegalStateException("Portal with identifier $startIdentifier not found")
        val end = map.values.find { it.portal?.identifier == endIdentifier } ?: throw IllegalStateException("Portal with identifier $endIdentifier not found")

        return findMinimumSteps(start.coordinate, end.coordinate, map, recursive)
    }

    private fun findMinimumSteps(start: Coordinate, end: Coordinate, map: Map<Coordinate, Node>, recursiveMode: Boolean): Int {
        // Breadth first search
        val leveledStart = LeveledCoordinate(start, 0)
        val leveledEnd = LeveledCoordinate(end, 0)

        return reachableNodes(leveledStart, neighbours = { leveledCoordinate ->
                val neighbours = map[leveledCoordinate.coordinate]?.neighbours(leveledCoordinate.level, recursiveMode) ?: emptyList()

                FilteredIterable(neighbours) { map[it.coordinate]?.isReachable(it.level, recursiveMode) ?: false }
            },
            process = { nodePath ->
                if (nodePath.node == leveledEnd) nodePath.pathLength else null
            }
        ) ?: throw IllegalStateException("End not found")
    }
}