package it.unibo.collektive.entrypoint

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveSize
import it.unibo.collektive.Collektive
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.AggregateContext
import it.unibo.collektive.field.Field
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager

class CollektiveTest : StringSpec({
    val id0 = IntId(0)
    val id1 = IntId(1)

    val initV1 = 1
//    val initV2 = 2

    val increaseOrDouble: (Field<Int>) -> Field<Int> = { f ->
        f.mapWithId { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
    }

    val networkManager = NetworkManager()
    val network0 = NetworkImplTest(networkManager, id0)
    val network1 = NetworkImplTest(networkManager, id1)

//    val computeFunction0: AggregateContext.() -> Int = {
//        exchange(initV1, increaseOrDouble).localValue
//    }


//    val computeFunction1: AggregateContext.() -> Int = {
//        exchange(initV2, increaseOrDouble).localValue
//    }

    "Collektive should receive and send messages with cycle function" {
        fun AggregateContext.computeFunction1(): Int = exchange(initV1, increaseOrDouble).localValue
        val collektiveDevice0 = Collektive(id0, network0) { computeFunction1() }
        println("net0 ${network0.read()}")
        val res0 = collektiveDevice0.cycle()
        println("network for id0 ${network0.read()}")
        println("res0 $res0")
        network0.read() shouldHaveSize 0

        val collektiveDevice1 = Collektive(id1, network1) {
            exchange(initV1, increaseOrDouble).localValue
        }
        println("net1 ${network1.read()}")
        val res1 = collektiveDevice1.cycle()
        network1.read() shouldHaveSize 1
        val r = network0.read().first()
        r.messages.keys.forEach { e -> e.path shouldHaveAtLeastSize 1 }
        println("network for id1 ${network1.read()}")
        println("res1 $res1")
    }
})
