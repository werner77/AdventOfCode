package com.behindmedia.adventofcode.common

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlin.test.BeforeTest
import kotlin.test.Test

class DefaultMapTest {

    private lateinit var map: DefaultMutableMap<String, Int>

    @BeforeTest
    fun setup() {
        map = defaultMutableMapOf(putValueImplicitly = true) { 0 }
    }

    @Test
    fun size() {
        assertEquals(0, map.size)
        map[""]
        assertEquals(1, map.size)
    }

    @Test
    fun contains() {
        assertFalse(map.containsKey(""))
        assertFalse(map.containsValue(0))
        map[""]
        assertTrue(map.containsKey(""))
        assertTrue(map.containsValue(0))
    }

    @Test
    fun put() {
        assertFalse(map.containsKey(""))
        assertFalse(map.containsValue(1))
        val oldValue = map.put("", 1)
        assertNull(oldValue)
        assertTrue(map.containsKey(""))
        assertTrue(map.containsValue(1))
        assertFalse(map.containsValue(0))
    }

    @Test
    fun computeIfAbsent() {
        assertEquals(0, map.computeIfAbsent("") { 1 })
    }

    @Test
    fun getOrPutDefault() {
        assertFalse(map.containsKey(""))
        assertEquals(0, map.getOrPutDefault(""))
        // Should not put in the map
        assertEquals(0, map[""])
    }

    @Test
    fun getOrPut() {
        assertFalse(map.containsKey(""))
        assertEquals(0, map.getOrPut("") { 1 })
    }

    @Test
    fun computeIfPresent() {
        assertEquals(1, map.computeIfPresent("") { _, _ -> 1 })
    }
}