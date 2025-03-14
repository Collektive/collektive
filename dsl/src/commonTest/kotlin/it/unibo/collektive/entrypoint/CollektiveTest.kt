package it.unibo.collektive.entrypoint

import it.unibo.collektive.Collektive
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.Aggregate.Companion.exchange
import it.unibo.collektive.field.Field
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import kotlin.test.Test
import kotlin.test.assertEquals

class CollektiveTest {
    val id0 = 0
    val id1 = 1

    val initV1 = 1
    val initV2 = 2
    val initV3 = 3

    val increaseOrDouble: (Field<Int, Int>) -> Field<Int, Int> = { f ->
        f.mapWithId { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
    }

    val computeFunctionDevice0: Aggregate<Int>.() -> Int = {
        exchange(initV2, increaseOrDouble).localValue
    }

    fun Aggregate<Int>.computeFunctionDevice1(): Int = exchange(initV3, increaseOrDouble).localValue

    @Test
    fun `One Collektive device with cycle as entrypoint should work fine`() {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, id0)
        val collectiveDevice =
            Collektive(id0, network0) {
                exchange(initV1, increaseOrDouble).localValue
            }

        val result = collectiveDevice.cycle()
        assertEquals(2, result)
    }

    @Test
    fun `One Collektive device with cycleWhile as entrypoint should return the cycled result`() {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, id0)
        val collectiveDevice =
            Collektive(id0, network0) {
                exchange(initV1, increaseOrDouble).localValue
            }

        val result = collectiveDevice.cycleWhile { it.result < 10 }
        assertEquals(14, result)
    }

    @Test
    fun `Collektive compute function can be a val`() {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, id0)
        val collectiveDevice = Collektive(id0, network0, computeFunction = computeFunctionDevice0)

        val result = collectiveDevice.cycle()
        assertEquals(3, result)
    }

    @Test
    fun `Collektive compute function can be a function`() {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, id0)
        val collectiveDevice = Collektive(id0, network0) { computeFunctionDevice1() }

        val result = collectiveDevice.cycle()
        assertEquals(6, result)
    }

    @Test
    fun `Two Collektive aligned devices with cycle as entrypoint should exchange messages and results fine`() {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, id0)
        val network1 = NetworkImplTest(networkManager, id1)

        val collektiveDevice0 = Collektive(id0, network0, computeFunction = computeFunctionDevice0)
        val collektiveDevice1 = Collektive(id1, network1, computeFunction = computeFunctionDevice0)

        assertEquals(3, collektiveDevice0.cycle())
        assertEquals(3, collektiveDevice1.cycle())
    }

    @Test
    fun `Two Collektive aligned devices with cycleWhile as entrypoint should exchange messages and results fine`() {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, id0)
        val network1 = NetworkImplTest(networkManager, id1)

        val collektiveDevice0 = Collektive(id0, network0, computeFunction = computeFunctionDevice0)
        val collektiveDevice1 = Collektive(id1, network1, computeFunction = computeFunctionDevice0)

        assertEquals(6, collektiveDevice0.cycleWhile { it.result < 6 })
        assertEquals(14, collektiveDevice1.cycleWhile { it.result < 10 })
    }

    @Test
    fun `Two Collektive aligned devices with cycle as entrypoint should exchange messages in more than one cycle`() {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, id0)
        val network1 = NetworkImplTest(networkManager, id1)
        val collektiveDevice0 =
            Collektive(id0, network0) {
                exchange(1, increaseOrDouble).localValue
            }
        val collektiveDevice1 =
            Collektive(id1, network1) {
                exchange(2, increaseOrDouble).localValue
            }
        // from its initial value 1, apply increaseOrDouble, then sends to device1
        assertEquals(2, collektiveDevice0.cycle())
        // from its initial value 2, apply increaseOrDouble, then sends to device0
        assertEquals(3, collektiveDevice1.cycle())
        // from its value after first cycle 2, apply increaseOrDouble, then sends to device1
        assertEquals(3, collektiveDevice0.cycle())
        // from its value after first cycle 3, apply increaseOrDouble, then sends to device1
        assertEquals(6, collektiveDevice1.cycle())
    }

    @Test
    fun `Two unaligned Collektive devices should not interfere with each others`() {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, id0)
        val network1 = NetworkImplTest(networkManager, id1)

        val collektiveDevice0 = Collektive(id0, network0, computeFunction = computeFunctionDevice0)
        val collektiveDevice1 = Collektive(id1, network1) { computeFunctionDevice1() }

        assertEquals(3, collektiveDevice0.cycle())
        assertEquals(6, collektiveDevice1.cycle())

        // if those devices were aligned, the result of the computation would have been different
        assertEquals(6, collektiveDevice0.cycle())
        assertEquals(7, collektiveDevice1.cycle())
    }
}
