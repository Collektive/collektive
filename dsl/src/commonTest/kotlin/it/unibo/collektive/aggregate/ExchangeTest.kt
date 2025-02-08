package it.unibo.collektive.aggregate

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate.Companion.exchange
import it.unibo.collektive.aggregate.api.Aggregate.Companion.exchanging
import it.unibo.collektive.field.ConstantField
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.stdlib.ints.FieldedInts.plus
import it.unibo.collektive.testing.mooreGrid
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExchangeTest {
    private val increaseOrDouble: (Field<Int, Int>) -> Field<Int, Int> = { f ->
        f.mapWithId { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
    }

    private fun mooreGridWithIncreaseOrDouble(size: Int) =
        mooreGrid<Int>(size, size, { _, _ -> Int.MAX_VALUE }) {
            exchange(1) { field ->
                field.map { field.fold(localId) { acc, value -> acc + value } }
            }.localValue
        }.also {
            assertEquals(size * size, it.nodes.size)
        }

    private fun mooreGridWithConstantFieldResult(size: Int) =
        mooreGrid<Field<Int, Int>>(size, size, { _, _ -> Field.invoke(0, 0, emptyMap()) }) {
            exchange(1) { field ->
                field.mapToConstantField(10)
            }
        }.also {
            assertEquals(size * size, it.nodes.size)
        }

    @Test
    fun `exchange on the first round should use the initial value and send the new value according to the function`() {
        val result =
            aggregate(0) {
                val res = exchange(1, increaseOrDouble)
                assertEquals(2, res.localValue)
            }
        val messages = result.toSend.prepareMessageFor(1).sharedData
        assertEquals(1, messages.size)
        assertContentEquals(listOf(2), messages.values)
    }

    @Test
    fun `exchange should share data with other aligned devices in the network`() {
        val environment = mooreGridWithIncreaseOrDouble(2)
        environment.cycleInOrder()
        val result = environment.status()

        /**
         * Each node id connected to the others in the network supposing to fire the round in order:
         * 0=ϕ(localId=0, localValue=0, neighbors={})
         * 1=ϕ(localId=1, localValue=1, neighbors={0=1})
         * 2=ϕ(localId=2, localValue=3, neighbors={0=3, 1=3})
         * 3=ϕ(localId=3, localValue=7, neighbors={0=7, 1=7, 2=7})
         */
        val expectedResult = mapOf(0 to 0, 1 to 1, 2 to 3, 3 to 7)
        assertEquals(expectedResult, result)

        //        val networkManager = NetworkManager()
        //
        //        // Device 1
        //        val testNetwork1 = NetworkImplTest(networkManager, 1)
        //        val resultDevice1 =
        //            aggregate(1, testNetwork1) {
        //                val res1 = exchange(1, increaseOrDouble)
        //                val res2 = exchange(2, increaseOrDouble)
        //                testNetwork1.currentInbound().neighbors shouldHaveSize 0
        //                res1.localValue shouldBe 2
        //                res2.localValue shouldBe 3
        //            }
        //        val messagesFor2 = resultDevice1.toSend.prepareMessageFor(2).sharedData
        //        messagesFor2 shouldHaveSize 2
        //        messagesFor2.values.toList() shouldBe listOf(2, 3)
        //
        //        // Device 2
        //        val testNetwork2 = NetworkImplTest(networkManager, 2)
        //        val resultDevice2 =
        //            aggregate(2, testNetwork2) {
        //                val res1 = exchange(3, increaseOrDouble)
        //                val res2 = exchange(4, increaseOrDouble)
        //                res1.localValue shouldBe 6
        //                res2.localValue shouldBe 5
        //            }
        //        val messagesFor1 = resultDevice2.toSend.prepareMessageFor(1).sharedData
        //        val messagesForAnyoneElse = resultDevice2.toSend.prepareMessageFor(Int.MIN_VALUE).sharedData
        //        messagesFor1 shouldHaveSize 2
        //        messagesForAnyoneElse shouldHaveSize 2
        //        messagesFor1.values.toList() shouldBe listOf(3, 6)
        //        messagesForAnyoneElse.values.toList() shouldBe listOf(6, 5)
        //
        //        // Device 3
        //        val testNetwork3 = NetworkImplTest(networkManager, 3)
        //        val resultDevice3 =
        //            aggregate(3, testNetwork3) {
        //                val res1 = exchange(5, increaseOrDouble)
        //                val res2 = exchange(6, increaseOrDouble)
        //                res1.localValue shouldBe 10
        //                res2.localValue shouldBe 7
        //            }
        //        val messagesFrom3To1 = resultDevice3.toSend.prepareMessageFor(1).sharedData
        //        val messagesFrom3To2 = resultDevice3.toSend.prepareMessageFor(2).sharedData
        //        val messagesFrom3ToAnyoneElse = resultDevice3.toSend.prepareMessageFor(Int.MIN_VALUE).sharedData
        //        messagesFrom3To1 shouldHaveSize 2
        //        messagesFrom3To2 shouldHaveSize 2
        //        messagesFrom3ToAnyoneElse shouldHaveSize 2
        //        messagesFrom3ToAnyoneElse.values.toList() shouldBe listOf(10, 7)
        //        messagesFrom3To1.values.toList() shouldBe listOf(3, 6)
        //        messagesFrom3To2.values.toList() shouldBe listOf(7, 10)
    }

    @Test
    fun `exchange can yield a result but return a different value`() {
        val result =
            aggregate(0) {
                val xcRes =
                    exchanging(1) {
                        val fieldResult = it + 1
                        fieldResult.yielding { fieldResult.map { value -> "return: $value" } }
                    }
                assertEquals(mapOf(0 to "return: 2"), xcRes.toMap())
            }
        val messages = result.toSend.prepareMessageFor(1).sharedData
        assertEquals(1, messages.size)
        assertContentEquals(listOf(2), messages.values)
    }

    @Test
    fun `exchange can yield a result of nullable values`() {
        val result =
            aggregate(0) {
                val xcRes =
                    exchanging(1) {
                        val fieldResult = it + 1
                        fieldResult.yielding { fieldResult.map { value -> value.takeIf { value > 10 } } }
                    }
                assertEquals(mapOf(0 to null), xcRes.toMap())
            }
        val messages = result.toSend.prepareMessageFor(1).sharedData
        assertEquals(1, messages.size)
        assertContentEquals(listOf(2), messages.values)
    }

    @Test
    fun `exchange should produce a message with no overrides when producing a constant field`() {
        val size = 2
        val environment = mooreGridWithConstantFieldResult(size)
        // Executes two rounds per device
        environment.cycleInOrder()
        environment.cycleInOrder()
        val result = environment.status()
        assertEquals(1, result.values.distinct().size)
        assertTrue(result.values.all { it is ConstantField })
        assertEquals(listOf(10, 10, 10, 10), result.values.map { it.localValue })

        //        val programUnderTest: Aggregate<Int>.() -> Unit = {
        //            exchanging(0) {
        //                it.mapToConstantField(10).yielding { it.mapToConstantField("singleton") }
        //            }
        //        }
        //        val networkManager = NetworkManager()
        //
        //        // Three devices linked together executed for 2 round each.
        //        (0..5).forEach { iteration ->
        //            val id = iteration % 3
        //            val res =
        //                aggregate(id, emptyMap(), networkManager.receiveMessageFor(id), compute = programUnderTest)
        //                    .also { networkManager.send(id, it.toSend) }
        //            val toUnknown = res.toSend.prepareMessageFor(Int.MIN_VALUE).sharedData
        //            toUnknown shouldHaveSize 1
        //            val next = res.toSend.prepareMessageFor((id + 1) % 3).sharedData
        //            val previous = res.toSend.prepareMessageFor((id + 2) % 3).sharedData
        //            // When a constant field is used, the map of overrides should be empty
        //            next shouldBe toUnknown
        //            previous shouldBe toUnknown
        //        }
    }
}
