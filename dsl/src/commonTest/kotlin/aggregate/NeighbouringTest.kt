package aggregate

import Network
import NetworkImpl
import aggregate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NeighbouringTest {
    private val double: (Int) -> Int = { it * 2 }
    private val testValue: Int = 1
    private val testValue2: Int = 2

    @Test
    fun neighbouringWithoutMessages() {
        aggregate {
            val field = neighbouring(double(testValue))
            assertTrue(field.toMap().containsValue(2))
        }
    }

    @Test
    fun neighbouringWithTwoAlignedDevices() {
        val network: Network = NetworkImpl()

        var i = 0
        val condition: () -> Boolean = { i++ < 1 }

        // Device 1
        aggregate(condition, network) {
            val field = neighbouring(double(testValue))
            assertTrue(field.toMap().containsValue(2))
        }

        i = 0
        // Device 2
        aggregate(condition, network) {
            val field = neighbouring(double(testValue2))
            assertTrue(field.toMap().containsValue(2))
            assertTrue(field.toMap().containsValue(4))
        }
    }

    @Test
    fun neighbouringWithTwoNonAlignedDevices() {
        val network: Network = NetworkImpl()

        var i = 0
        val condition: () -> Boolean = { i++ < 1 }

        // Device 1
        val isDeviceOneKing = true
        aggregate(condition, network) {
            fun kingBehaviour() = neighbouring(double(testValue))
            fun queenBehaviour() = neighbouring(double(testValue))
            if (isDeviceOneKing) kingBehaviour() else queenBehaviour()
        }

        i = 0
        // Device 2
        val isDeviceTwoKing = false
        aggregate(condition, network) {
            fun kingBehaviour() = neighbouring(double(testValue))
            fun queenBehaviour() = neighbouring(double(testValue))
            val field = if (isDeviceTwoKing) kingBehaviour() else queenBehaviour()
            assertEquals(1, field.toMap().size)
        }
    }

}