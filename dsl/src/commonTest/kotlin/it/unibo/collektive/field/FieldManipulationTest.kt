package it.unibo.collektive.field

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.field.operations.max
import it.unibo.collektive.field.operations.maxWithSelf
import it.unibo.collektive.field.operations.min
import it.unibo.collektive.field.operations.minWithSelf
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldManipulationTest {
    private val double: (Int) -> Int = { it * 2 }

    // ids
    private val id0 = 0
    private val id1 = 1

    @Test
    fun `get the min value including self`() {
        aggregate(id0) {
            val sharedField = neighboringViaExchange(double(3))
            assertEquals(6, sharedField.minWithSelf())
        }

        aggregate(id1) {
            val sharedField = neighboringViaExchange(double(2))
            assertEquals(4, sharedField.minWithSelf())
        }
    }

    @Test
    fun `get the max value including self issue 767`() {
        val nm = NetworkManager()
        val network0 = NetworkImplTest(nm, id0)
        val network1 = NetworkImplTest(nm, id1)

        aggregate(id0, network0) {
            val sharedField = neighboringViaExchange(double(3))
            assertEquals(6, sharedField.maxWithSelf())
        }

        aggregate(id1, network1) {
            val sharedField = neighboringViaExchange(double(4))
            assertEquals(8, sharedField.maxWithSelf())
        }
    }

    @Test
    fun `get min value non including self`() {
        val nm = NetworkManager()
        val network0 = NetworkImplTest(nm, id0)
        val network1 = NetworkImplTest(nm, id1)

        aggregate(id0, network0) {
            val minValue = neighboringViaExchange(double(3)).min(Int.MAX_VALUE)
            assertEquals(Int.MAX_VALUE, minValue)
        }

        aggregate(id1, network1) {
            val minValue = neighboringViaExchange(double(2)).min(Int.MAX_VALUE)
            assertEquals(6, minValue)
        }
    }

    @Test
    fun `get max value non including self`() {
        val nm = NetworkManager()
        val network0 = NetworkImplTest(nm, id0)
        val network1 = NetworkImplTest(nm, id1)

        aggregate(id0, network0) {
            val maxValue = neighboringViaExchange(double(3)).max(Int.MIN_VALUE)
            assertEquals(Int.MIN_VALUE, maxValue)
        }

        aggregate(id1, network1) {
            val maxValue = neighboringViaExchange(double(2)).max(Int.MIN_VALUE)
            assertEquals(6, maxValue)
        }
    }
}
