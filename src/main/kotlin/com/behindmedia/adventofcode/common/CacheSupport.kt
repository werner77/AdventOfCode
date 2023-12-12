package com.behindmedia.adventofcode.common

import java.util.concurrent.ConcurrentHashMap


object CacheSupport {
    private val _cache = ConcurrentHashMap<Any, MutableMap<*, *>>()
    fun <T, R>withCaching(arg: T, cacheOwner: Any = Thread.currentThread(), perform: (T) -> R): R {
        var created = false
        @Suppress("UNCHECKED_CAST")
        val cache: MutableMap<T, R> = _cache.computeIfAbsent(cacheOwner) {
            created = true
            hashMapOf<T, R>()
        } as MutableMap<T, R>
        try {
            return cache.getOrPut(arg) {
                perform(arg)
            }
        } finally {
            if (created) {
                _cache.remove(cacheOwner)
            }
        }
    }
}