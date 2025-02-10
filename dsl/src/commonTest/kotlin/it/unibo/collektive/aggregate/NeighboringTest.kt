package it.unibo.collektive.aggregate

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.Aggregate.Companion.neighboring
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.operations.all
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NeighboringTest {
    private fun mooreGrid(
        size: Int,
        program: Aggregate<Int>.(Environment<Field<Int, Int>>, Int) -> Field<Int, Int>,
    ) = mooreGrid(size, size, { _, _ -> Field(Int.MAX_VALUE, 0) }, program).also {
        assertEquals(size * size, it.nodes.size)
    }

    @Test
    fun `neighboring must produce a field with the local value when no neighbours are present`() {
        aggregate(0) {
            val field = neighboringViaExchange(1)
            assertContains(field.toMap().values, 1)
            assertEquals(1, field.localValue)
        }
    }

    @Test
    fun `optimized neighboring must produce a field with the local value when no neighbours are present`() {
        aggregate(0) {
            val field = neighboring(1)
            assertContains(field.toMap().values, 1)
            assertEquals(1, field.localValue)
        }
    }

    @Test
    fun `neighboring should build a field containing the values of the aligned neighbors`() {
        val size = 5
        val environment =
            mooreGrid(size) { _, _ ->
                neighboringViaExchange(1)
            }
        repeat(size - 1) {
            environment.cycleInOrder()
        }
        environment.status().forEach { (_, field) ->
            assertEquals(1, field.localValue)
            assertTrue(field.all { it == 1 })
        }
    }

    @Test
    fun `optimized neighboring should build a field containing the values of the aligned neighbors`() {
        val size = 10
        val environment =
            mooreGrid(size) { _, _ ->
                neighboring(1)
            }
        repeat(size - 1) {
            environment.cycleInOrder()
        }
        environment.status().forEach { (_, field) ->
            assertEquals(1, field.localValue)
            assertTrue(field.all { it == 1 })
        }
    }

    @Test
    fun `only aligned devices should communicate each other`() {
        val size = 2
        val condition: (Int) -> Boolean = { it % 2 == 0 }
        val environment =
            mooreGrid(size) { _, id ->
                fun kingBehaviour() = neighboringViaExchange(1)

                fun queenBehaviour() = neighboringViaExchange(2)
                if (condition(id)) kingBehaviour() else queenBehaviour()
            }
        repeat(size - 1) {
            environment.cycleInOrder()
        }
        environment.status().forEach { (id, field) ->
            if (condition(id)) {
                assertEquals(1, field.localValue)
                assertTrue(field.all { it == 1 })
            } else {
                assertEquals(2, field.localValue)
                assertTrue(field.all { it == 2 })
            }
        }
    }

    @Test
    fun `only aligned devices should communicate each other with optimized neighboring`() {
        val size = 4
        val condition: (Int) -> Boolean = { it % 2 == 0 }
        val environment =
            mooreGrid(size) { _, id ->
                fun kingBehaviour() = neighboring(1)

                fun queenBehaviour() = neighboring(2)
                if (condition(id)) kingBehaviour() else queenBehaviour()
            }
        repeat(size - 1) {
            environment.cycleInOrder()
        }
        environment.status().forEach { (id, field) ->
            if (condition(id)) {
                assertEquals(1, field.localValue)
                assertTrue(field.all { it == 1 })
            } else {
                assertEquals(2, field.localValue)
                assertTrue(field.all { it == 2 })
            }
        }
    }
}
