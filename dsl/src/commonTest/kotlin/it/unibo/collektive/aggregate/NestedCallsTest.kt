package it.unibo.collektive.aggregate

import it.unibo.collektive.Collektive
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.field.operations.min
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.stdlib.doubles.FieldedDoubles.plus
import kotlin.test.Test
import kotlin.test.assertEquals

class NestedCallsTest {
    fun Aggregate<Int>.foo(id: Int) = neighboringViaExchange(id.toDouble())

    fun Aggregate<Int>.bar(): Double =
        share(Double.POSITIVE_INFINITY) { dist ->
            when (localId) {
                0 -> 0.0
                else -> (foo(localId) + dist).min(Double.POSITIVE_INFINITY)
            }
        }

    @Test
    fun `regression test for the issue 207`() {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, 0)
        val network1 = NetworkImplTest(networkManager, 1)

        val collektiveDevice0 = Collektive(0, network0) { bar() }
        assertEquals(collektiveDevice0.cycle(), 0.0)

        val collektiveDevice1 = Collektive(1, network1) { bar() }
        assertEquals(Double.POSITIVE_INFINITY, collektiveDevice1.cycle())
    }
}
