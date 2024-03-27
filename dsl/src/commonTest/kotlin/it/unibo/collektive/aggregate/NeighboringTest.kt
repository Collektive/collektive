package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainValue
import io.kotest.matchers.maps.shouldHaveSize
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager

class NeighboringTest : StringSpec({
    // device ids
    val id0 = 0
    val id1 = 1
    val id2 = 2
    val id3 = 3

    // values
    val initV1 = 1
    val initV2 = 2
    val initV3 = 3
    val double: (Int) -> Int = { it * 2 }
    val add: (Int) -> Int = { it + 1 }

    "Neighboring must produce a field with the local value when no neighbours are present" {
        aggregate(id0) {
            val field = neighboringViaExchange(initV1)
            field.toMap() shouldContainValue initV1
        }
    }

    "Optimized neighboring must produce a field with the local value when no neighbours are present" {
        aggregate(id0) {
            val field = neighboring(initV1)
            field.toMap() shouldContainValue initV1
        }
    }

    "Neighboring works across three aligned devices" {
        val nm = NetworkManager()

        // Device 1
        val testNetwork1 = NetworkImplTest(nm, id1)
        aggregate(id1, testNetwork1) {
            val field = neighboringViaExchange(double(initV1))
            field.toMap() shouldContainValue 2
        }

        // Device 2
        val testNetwork2 = NetworkImplTest(nm, id2)
        aggregate(id2, testNetwork2) {
            val field = neighboringViaExchange(double(initV2))
            field.toMap() shouldContainValue 2
            field.toMap() shouldContainValue 4
        }

        // Device 3
        val testNetwork3 = NetworkImplTest(nm, id3)
        aggregate(id3, testNetwork3) {
            val field = neighboringViaExchange(double(initV3))
            field.toMap() shouldContainValue 4
            field.toMap() shouldContainValue 6
        }
    }

    "Optimized neighboring works across three aligned devices" {
        val nm = NetworkManager()

        // Device 1
        val testNetwork1 = NetworkImplTest(nm, id1)
        aggregate(id1, testNetwork1) {
            val field = neighboring(double(initV1))
            field.toMap() shouldContainValue 2
        }

        // Device 2
        val testNetwork2 = NetworkImplTest(nm, id2)
        aggregate(id2, testNetwork2) {
            val field = neighboring(double(initV2))
            field.toMap() shouldContainValue 2
            field.toMap() shouldContainValue 4
        }

        // Device 3
        val testNetwork3 = NetworkImplTest(nm, id3)
        aggregate(id3, testNetwork3) {
            val field = neighboring(double(initV3))
            field.toMap() shouldContainValue 4
            field.toMap() shouldContainValue 6
        }
    }

    "Non-aligned devices do not communicate" {
        val nm = NetworkManager()

        // Device 1
        val isDeviceOneKing = true
        val testNetwork1 = NetworkImplTest(nm, id1)
        aggregate(id1, testNetwork1) {
            fun kingBehaviour() = neighboringViaExchange(double(initV2))

            fun queenBehaviour() = neighboringViaExchange(add(initV1))
            val f = if (isDeviceOneKing) kingBehaviour() else queenBehaviour()
            f.toMap() shouldHaveSize 1
        }

        // Device 2
        val isDeviceTwoKing = false
        val testNetwork2 = NetworkImplTest(nm, id2)
        aggregate(id2, testNetwork2) {
            fun kingBehaviour() = neighboringViaExchange(double(initV1))

            fun queenBehaviour() = neighboringViaExchange(add(initV2))
            val field = if (isDeviceTwoKing) kingBehaviour() else queenBehaviour()
            field.toMap() shouldHaveSize 1
        }
    }

    "Non-aligned devices do not communicate with optimized neighboring" {
        val nm = NetworkManager()

        // Device 1
        val isDeviceOneKing = true
        val testNetwork1 = NetworkImplTest(nm, id1)
        aggregate(id1, testNetwork1) {
            fun kingBehaviour() = neighboring(double(initV2))

            fun queenBehaviour() = neighboring(add(initV1))
            val f = if (isDeviceOneKing) kingBehaviour() else queenBehaviour()
            f.toMap() shouldHaveSize 1
        }

        // Device 2
        val isDeviceTwoKing = false
        val testNetwork2 = NetworkImplTest(nm, id2)
        aggregate(id2, testNetwork2) {
            fun kingBehaviour() = neighboring(double(initV1))

            fun queenBehaviour() = neighboring(add(initV2))
            val field = if (isDeviceTwoKing) kingBehaviour() else queenBehaviour()
            field.toMap() shouldHaveSize 1
        }
    }
})
