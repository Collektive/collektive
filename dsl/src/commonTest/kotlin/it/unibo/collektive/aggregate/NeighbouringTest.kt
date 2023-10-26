package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainValue
import io.kotest.matchers.maps.shouldHaveSize
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate
import it.unibo.collektive.neighbouring
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.networking.NetworkManager

class NeighbouringTest : StringSpec({
    // device ids
    val id0 = IntId(0)
    val id1 = IntId(1)
    val id2 = IntId(2)
    val id3 = IntId(3)

    // values
    val initV1 = 1
    val initV2 = 2
    val initV3 = 3
    val double: (Int) -> Int = { it * 2 }
    val add: (Int) -> Int = { it + 1 }

    "Neighbouring without messages" {
        aggregate(id0) {
            val field = neighbouring(initV1)
            field.toMap() shouldContainValue initV1
        }
    }

    "Neighbouring with three aligned devices" {
        val nm = NetworkManager()
        var i = 0
        val condition: () -> Boolean = { i++ < 1 }

        // Device 1
        val testNetwork1 = NetworkImplTest(nm, id1)
        aggregate(id1, condition, testNetwork1) {
            val field = neighbouring(double(initV1))
            println("field 1 $field")
            field.toMap() shouldContainValue 2
        }

        i = 0
        // Device 2
        val testNetwork2 = NetworkImplTest(nm, id2)
        aggregate(id2, condition, testNetwork2) {
            val field = neighbouring(double(initV2))
            println("field 2 $field")
            field.toMap() shouldContainValue 2
            field.toMap() shouldContainValue 4
        }

        i = 0
        // Device 3
        val testNetwork3 = NetworkImplTest(nm, id3)
        aggregate(id3, condition, testNetwork3) {
            val field = neighbouring(double(initV3))
            println("field 3 $field")
            field.toMap() shouldContainValue 2
            field.toMap() shouldContainValue 4
            field.toMap() shouldContainValue 6
        }
    }

    "Neighbouring with two not aligned devices" {
        val nm = NetworkManager()
        var i = 0
        val condition: () -> Boolean = { i++ < 1 }

        // Device 1
        val isDeviceOneKing = true
        val testNetwork1 = NetworkImplTest(nm, id1)
        aggregate(id1, condition, testNetwork1) {
            fun kingBehaviour() = neighbouring(double(initV2))
            fun queenBehaviour() = neighbouring(add(initV1))
            val f = if (isDeviceOneKing) kingBehaviour() else queenBehaviour()
            println("f $f")
            f.toMap() shouldHaveSize 1
        }

        i = 0
        // Device 2
        val isDeviceTwoKing = false
        val testNetwork2 = NetworkImplTest(nm, id2)
        aggregate(id2, condition, testNetwork2) {
            fun kingBehaviour() = neighbouring(double(initV1))
            fun queenBehaviour() = neighbouring(add(initV2))
            val field = if (isDeviceTwoKing) kingBehaviour() else queenBehaviour()
            println("field $field")
            field.toMap() shouldHaveSize 1
        }
    }
})
