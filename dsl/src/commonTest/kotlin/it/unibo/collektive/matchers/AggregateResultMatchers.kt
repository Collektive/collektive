package it.unibo.collektive.matchers

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.networking.NoNeighborsData
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Asserts that the [firstProgram] and [secondProgram] align.
 */
fun <Result> assertAligned(
    firstProgram: Aggregate<Int>.() -> Result,
    secondProgram: Aggregate<Int>.() -> Result,
) {
    val first =
        aggregate(0, emptyMap(), NoNeighborsData(), compute = firstProgram)
            .run { toSend.prepareMessageFor(this.localId).sharedData.keys }
    val second =
        aggregate(0, emptyMap(), NoNeighborsData(), compute = secondProgram)
            .run { toSend.prepareMessageFor(this.localId).sharedData.keys }
    assertEquals(first, second)
}

/**
 * Asserts that the [firstProgram] and [secondProgram] do not align.
 */
fun <First, Second> assertNotAligned(
    firstProgram: Aggregate<Int>.() -> First,
    secondProgram: Aggregate<Int>.() -> Second,
) {
    val first =
        aggregate(0, emptyMap(), NoNeighborsData(), compute = firstProgram)
            .run { toSend.prepareMessageFor(this.localId).sharedData.keys }
    val second =
        aggregate(0, emptyMap(), NoNeighborsData(), compute = secondProgram)
            .run { toSend.prepareMessageFor(this.localId).sharedData.keys }
    assertNotEquals(first, second)
}
