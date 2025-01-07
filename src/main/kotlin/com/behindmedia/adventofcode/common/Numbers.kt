package com.behindmedia.adventofcode.common

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
            result - b.value != this.value
        }
        return SafeLong(result)
    }

    operator fun minus(b: SafeLong): SafeLong {
        val result = this.value - b.value
        checkOverflow {
            result + b.value != this.value
        }
        return SafeLong(result)
    }

    operator fun times(b: SafeLong): SafeLong {
        val result = this.value * b.value
        checkOverflow {
            this.value != result / b.value
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
            result - b.value != this.value
        }
        return SafeInt(result)
    }

    operator fun minus(b: SafeInt): SafeInt {
        val result = this.value - b.value
        checkOverflow {
            result + b.value != this.value
        }
        return SafeInt(result)
    }

    operator fun times(b: SafeInt): SafeInt {
        val result = this.value * b.value
        checkOverflow {
            this.value != result / b.value
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