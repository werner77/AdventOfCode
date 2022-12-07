package com.behindmedia.adventofcode.common

data class Quadruple<out A, out B, out C, out D>(val first: A, val second: B, val third: C, val fourth: D)
data class Quintuple<out A, out B, out C, out D, out E>(val first: A, val second: B, val third: C, val fourth: D, val
fifth: E)

inline fun <A : Any, R: Any> whenNotNull(
    a: A?, perform: (A) -> R
): R? {
    if (a != null) {
        return perform(a)
    }
    return null
}

inline fun <A : Any, B : Any, R: Any> whenNotNull(
    a: A?, b: B?, perform: (A, B) -> R
): R? {
    if (a != null && b != null) {
        return perform(a, b)
    }
    return null
}

inline fun <A : Any, B : Any, C : Any, R: Any> whenNotNull(
    a: A?, b: B?, c: C?, perform: (A, B, C) -> R
): R? {
    if (a != null && b != null && c != null) {
        return perform(a, b, c)
    }
    return null
}

inline fun <A : Any, B : Any, C : Any, D : Any, R: Any> whenNotNull(
    a: A?, b: B?, c: C?, d: D?, perform: (A, B, C, D) -> R
): R? {
    if (a != null && b != null && c != null && d != null) {
        return perform(a, b, c, d)
    }
    return null
}

inline fun <A : Any, B : Any, C : Any, D : Any, E : Any, R: Any> whenNotNull(
    a: A?, b: B?, c: C?, d: D?, e: E?, perform: (A, B, C, D, E) -> R
): R? {
    if (a != null && b != null && c != null && d != null && e != null) {
        return perform(a, b, c, d, e)
    }
    return null
}

inline fun <A : Any> guardNotNull(
    a: A?, elsePerform: () -> Nothing
): A {
    if (a != null) {
        return a
    }
    elsePerform()
}

inline fun <A : Any, B : Any> guardNotNull(
    a: A?, b: B?, elsePerform: () -> Nothing
): Pair<A, B> {
    if (a != null && b != null) {
        return Pair(a, b)
    }
    elsePerform()
}

inline fun <A : Any, B : Any, C: Any> guardNotNull(
    a: A?, b: B?, c: C?, elsePerform: () -> Nothing
): Triple<A, B, C> {
    if (a != null && b != null && c != null) {
        return Triple(a, b, c)
    }
    elsePerform()
}

inline fun <A : Any, B : Any, C: Any, D: Any> guardNotNull(
    a: A?, b: B?, c: C?, d: D?, elsePerform: () -> Nothing
): Quadruple<A, B, C, D> {
    if (a != null && b != null && c != null && d != null) {
        return Quadruple(a, b, c, d)
    }
    elsePerform()
}

inline fun <A : Any, B : Any, C: Any, D: Any, E: Any> guardNotNull(
    a: A?, b: B?, c: C?, d: D?, e: E?, elsePerform: () -> Nothing
): Quintuple<A, B, C, D, E> {
    if (a != null && b != null && c != null && d != null && e != null) {
        return Quintuple(a, b, c, d, e)
    }
    elsePerform()
}
