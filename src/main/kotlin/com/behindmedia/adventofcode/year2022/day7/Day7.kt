package com.behindmedia.adventofcode.year2022.day7

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.year2022.day7.Node.Dir
import com.behindmedia.adventofcode.year2022.day7.Node.File

private sealed class Node {

    var parent: Dir? = null
        private set

    abstract val name: String

    class File(override val name: String, val size: Long): Node()

    class Dir(override val name: String): Node() {

        private val _children = linkedMapOf<String, Node>()

        fun addChild(node: Node) {
            require(_children[node.name] == null) {
                "Already found child with name: $node.name"
            }
            _children[node.name] = node
            node.parent = this
        }

        fun getChild(name: String): Node? = _children[name]

        val totalSize: Long
            get() {
                var sum = 0L
                for (c in _children.values) {
                    sum += when (c) {
                        is File -> c.size
                        is Dir -> c.totalSize
                    }
                }
                return sum
            }

        val allContainedDirectories: Set<Dir>
            get() {
                val set = mutableSetOf<Dir>()
                set += this
                for (c in _children.values) {
                    when (c) {
                        is File -> {
                            //ignore
                        }
                        is Dir -> set += c.allContainedDirectories
                    }
                }
                return set
            }
    }
}

fun main() {
    val commandRegex = """\$ ([a-z]+) ?(.*)?""".toRegex()
    val fileRegex = """(\d+) (.*)""".toRegex()
    val dirRegex = """dir (.*)""".toRegex()
    val rootDir = Dir(name = "/")
    var currentDir: Dir = rootDir
    var listing = false

    parseLines("/2022/day7.txt") { line ->
        whenNotNull(commandRegex.matchEntire(line)) { match ->
            val command = match.groupValues[1]
            val arg = match.groupValues.getOrNull(2)
            listing = false
            when (command) {
                "cd" -> {
                    currentDir = when (val name = arg ?: error("No dir name found")) {
                        ".." -> {
                            currentDir.parent ?: error("No parent directory")
                        }
                        "/" -> {
                            rootDir
                        }
                        else -> {
                            currentDir.getChild(name) as? Dir ?: error("Dir with name $name not found")
                        }
                    }
                }
                "ls" -> {
                    listing = true
                }
                else -> error("Invalid command: $command")
            }
        } ?:
        whenNotNull(fileRegex.matchEntire(line)?.destructured) { (size, name) ->
            require(listing) { "Expected to be in listing mode" }
            currentDir.addChild(File(name, size.toLong()))
        } ?:
        whenNotNull(dirRegex.matchEntire(line)?.destructured) { (name) ->
            require(listing) { "Expected to be in listing mode" }
            if (name != "/") {
                currentDir.addChild(Dir(name))
            }
        } ?: run {
            error("Invalid line found: $line")
        }
    }

    part1(rootDir)
    part2(rootDir)
}

private fun part1(rootDir: Dir) {
    val ans = rootDir.allContainedDirectories.filter { it.totalSize <= 100_000 }.sumOf { it.totalSize }
    println(ans)
}

private fun part2(rootDir: Dir) {
    val sizeAvailable = 70000000 - rootDir.totalSize
    val sizeNeeded = 30000000 - sizeAvailable
    val ans = rootDir.allContainedDirectories.filter { it.totalSize >= sizeNeeded }.minOf { it.totalSize }
    println(ans)
}