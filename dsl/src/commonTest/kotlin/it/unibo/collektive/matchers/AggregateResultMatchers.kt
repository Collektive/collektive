package it.unibo.collektive.matchers

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.networking.NoNeighborsData
import it.unibo.collektive.path.Path
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

private fun <Result> getSharedDataKeys(program: Aggregate<Int>.() -> Result): Set<Path> =
    aggregate(0, emptyMap(), NoNeighborsData(), compute = program)
        .run { toSend.prepareMessageFor(this.localId).sharedData.keys }

/**
 * Asserts that the [firstProgram] and [secondProgram] align.
 */
fun <Result> assertAligned(firstProgram: Aggregate<Int>.() -> Result, secondProgram: Aggregate<Int>.() -> Result) {
    val first = getSharedDataKeys(firstProgram)
    val second = getSharedDataKeys(secondProgram)
    assertEquals(first, second)
}

/**
 * Asserts that the [firstProgram] and [secondProgram] do not align.
 */
fun <First, Second> assertNotAligned(
    firstProgram: Aggregate<Int>.() -> First,
    secondProgram: Aggregate<Int>.() -> Second,
) {
    val first = getSharedDataKeys(firstProgram)
    val second = getSharedDataKeys(secondProgram)
    assertNotEquals(first, second)
}
