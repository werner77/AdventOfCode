package com.behindmedia.adventofcode.common


object CacheSupport {
    private val _cache = ThreadLocal<MutableMap<*, *>>()
    fun <T, R>withCaching(arg: T, perform: (T) -> R): R {
        @Suppress("UNCHECKED_CAST")
        val existingCache: MutableMap<T, R>? = _cache.get() as? MutableMap<T, R>
        val cache = existingCache ?: hashMapOf<T, R>().also {
            _cache.set(it)
        }
        try {
            return cache.getOrPut(arg) {
                perform(arg)
            }
        } finally {
            if (existingCache == null) {
                _cache.remove()
            }
        }
    }
}