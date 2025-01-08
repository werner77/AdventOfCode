package com.behindmedia.adventofcode.common

import kotlin.math.sign

@JvmInline
value class SafeLong(val value: Long) : Comparable<SafeLong> {
    operator fun unaryPlus(): SafeLong {
        return SafeLong(value)
    }

    operator fun unaryMinus(): SafeLong {
        checkOverflow {
            value == Long.MIN_VALUE
        }
        return SafeLong(-value)
    }

    operator fun inc(): SafeLong {
        checkOverflow {
            value == Long.MAX_VALUE
        }
        return SafeLong(value + 1)
    }

    operator fun dec(): SafeLong {
        checkOverflow {
            value == Long.MIN_VALUE
        }
        return SafeLong(value - 1)
    }

    operator fun plus(b: SafeLong): SafeLong {
        val result = this.value + b.value
        checkOverflow {
            if (this.value > 0 && b.value > 0) {
                result < 0
            } else if (this.value < 0 && b.value < 0) {
                result > 0
            } else {
                false
            }
        }
        return SafeLong(result)
    }

    operator fun minus(b: SafeLong): SafeLong {
        val result = this.value - b.value
        checkOverflow {
            if (this.value > 0 && b.value < 0) {
                result < 0
            } else if (this.value < 0 && b.value > 0) {
                result > 0
            } else {
                false
            }
        }
        return SafeLong(result)
    }

    operator fun times(b: SafeLong): SafeLong {
        val result = this.value * b.value
        checkOverflow {
            this.value != 0L && b.value != 0L && result.sign != (this.value.sign * b.value.sign)
        }
        return SafeLong(result)
    }

    operator fun div(b: SafeLong): SafeLong {
        return SafeLong(this.value / b.value)
    }

    operator fun rem(b: SafeLong): SafeLong {
        return SafeLong(this.value % (b.value))
    }

    override fun compareTo(other: SafeLong): Int {
        return value.compareTo(other.value)
    }

    fun toInt(): Int {
        return value.toInt()
    }
}

private inline fun checkOverflow(test: () -> Boolean) {
    if (test.invoke()) throw IllegalStateException("Overflow occurred")
}

@JvmInline
value class SafeInt(val value: Int) : Comparable<SafeInt> {
    operator fun unaryPlus(): SafeInt {
        return SafeInt(value)
    }

    operator fun unaryMinus(): SafeInt {
        checkOverflow {
            value == Integer.MIN_VALUE
        }
        return SafeInt(-value)
    }

    operator fun inc(): SafeInt {
        checkOverflow {
            value == Integer.MAX_VALUE
        }
        return SafeInt(value + 1)
    }

    operator fun dec(): SafeInt {
        checkOverflow {
            value == Integer.MIN_VALUE
        }
        return SafeInt(value - 1)
    }

    operator fun plus(b: SafeInt): SafeInt {
        val result = this.value + b.value
        checkOverflow {
            if (this.value > 0 && b.value > 0) {
                result < 0
            } else if (this.value < 0 && b.value < 0) {
                result > 0
            } else {
                false
            }
        }
        return SafeInt(result)
    }

    operator fun minus(b: SafeInt): SafeInt {
        val result = this.value - b.value
        checkOverflow {
            if (this.value > 0 && b.value < 0) {
                result < 0
            } else if (this.value < 0 && b.value > 0) {
                result > 0
            } else {
                false
            }
        }
        return SafeInt(result)
    }

    operator fun times(b: SafeInt): SafeInt {
        val result = this.value * b.value
        checkOverflow {
            this.value != 0 && b.value != 0 && result.sign != (this.value.sign * b.value.sign)
        }
        return SafeInt(result)
    }

    operator fun div(b: SafeInt): SafeInt {
        return SafeInt(this.value / b.value)
    }

    operator fun rem(b: SafeInt): SafeInt {
        return SafeInt(this.value % (b.value))
    }

    override fun compareTo(other: SafeInt): Int {
        return value.compareTo(other.value)
    }
}

fun Int.safe(): SafeInt = SafeInt(this)
fun Long.safe(): SafeLong = SafeLong(this)
fun Long.bounded(): BoundedLong = BoundedLong(this)

@JvmInline
value class BoundedLong(val value: Long) : Comparable<BoundedLong> {
    companion object {
        var MOD = 1_000_000_007L
        val ZERO = BoundedLong(0L)
        val ONE = BoundedLong(1L)
        val TWO = BoundedLong(2L)
        val FIVE = BoundedLong(5L)
        val TEN = BoundedLong(10L)
    }

    operator fun unaryPlus(): BoundedLong {
        return BoundedLong(value)
    }

    operator fun unaryMinus(): BoundedLong {
        return BoundedLong(-value)
    }

    operator fun inc(): BoundedLong {
        return BoundedLong(value + 1)
    }

    operator fun dec(): BoundedLong {
        return BoundedLong(value - 1)
    }

    operator fun plus(b: BoundedLong): BoundedLong {
        val result = (this.value + b.value) % MOD
        return BoundedLong(result)
    }

    operator fun minus(b: BoundedLong): BoundedLong {
        val result = (this.value - b.value) % MOD
        return BoundedLong(result)
    }

    operator fun times(b: BoundedLong): BoundedLong {
        val result = (this.value * b.value) % MOD
        return BoundedLong(result)
    }

    operator fun div(b: BoundedLong): BoundedLong {
        return BoundedLong(this.value / b.value)
    }

    operator fun rem(b: BoundedLong): BoundedLong {
        return BoundedLong(this.value % (b.value))
    }

    override fun compareTo(other: BoundedLong): Int {
        return value.compareTo(other.value)
    }

    fun toInt(): Int {
        return value.toInt()
    }

    fun toLong(): Long {
        return value
    }

    override fun toString(): String {
        return value.toString()
    }
}
