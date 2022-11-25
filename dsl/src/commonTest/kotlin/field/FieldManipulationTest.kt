package field

import Network
import NetworkImpl
import aggregate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldManipulationTest {
    private var network: Network = NetworkImpl()
    private val double: (Int) -> Int = { it * 2 }
    private var i = 0
    private val condition: () -> Boolean = { i++ < 1 }

    @BeforeTest
    fun setup() {
        // reset network and counter
        i = 0
        network = NetworkImpl()
    }

    @Test
    fun getMinIncludingSelf(){
        // Device 1
        aggregate(condition, network) {
            neighbouring(double(3))
        }

        i = 0
        // Device 2
        aggregate(condition, network) {
            val res = neighbouring(double(2)).min()
            assertEquals(4, res)
        }
    }

    @Test
    fun getMinNonIncludingSelf(){
        // Device 1
        aggregate(condition, network) {
            neighbouring(double(3))
        }

        i = 0
        // Device 2
        aggregate(condition, network) {
            val res = neighbouring(double(2)).min(includingSelf = false)
            assertEquals(6, res)
        }
    }

    @Test
    fun getMaxIncludingSelf(){
        // Device 1
        aggregate(condition, network) {
            neighbouring(double(3))
        }

        i = 0
        // Device 2
        aggregate(condition, network) {
            val res = neighbouring(double(2)).max()
            assertEquals(6, res)
        }
    }

    @Test
    fun getMaxNonIncludingSelf(){
        // Device 1
        aggregate(condition, network) {
            neighbouring(double(3))
        }

        i = 0
        // Device 2
        aggregate(condition, network) {
            val res = neighbouring(double(2)).max(includingSelf = false)
            assertEquals(6, res)
        }
    }
}