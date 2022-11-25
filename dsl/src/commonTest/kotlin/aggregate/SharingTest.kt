package aggregate

import field.Field
import Network
import NetworkImpl
import aggregate
import kotlin.test.Test
import kotlin.test.assertEquals

class SharingTest {
    private val initialValue: Int = 1
    private val initialValue2: Int = 2
    private val findMax: (Field<Int>) -> Int = { e -> e.toMap().maxBy { it.value }.value }

    @Test
    fun firstTimeSharing(){
        aggregate {
            val res = sharing(initialValue, findMax)
            assertEquals(1, res)
        }
    }

    @Test
    fun sharingWithTwoAlignedDevices(){
        val network: Network = NetworkImpl()

        var i = 0
        val condition: () -> Boolean = { i++ < 1 }

        // Device 1
        aggregate(condition, network) {
            sharing(initialValue, findMax)
        }

        i = 0
        // Device 2
        aggregate(condition, network) {
            val res = sharing(initialValue2, findMax)
            assertEquals(2, res)
        }
    }
}