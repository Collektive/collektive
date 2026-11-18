package it.unibo.collektive.matchers

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.alignment.Path
import it.unibo.collektive.aggregate.alignment.PerformanceProfile
import it.unibo.collektive.aggregate.api.Aggregate
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

private fun <Result> getSharedDataKeys(
    performanceProfile: PerformanceProfile,
    program: Aggregate<Int>.() -> Result,
): Set<Path> = aggregate(0, performanceProfile, compute = program)
    .run { toSend.prepareMessageFor(this.localId).sharedData.keys }

/**
 * Asserts that the [firstProgram] and [secondProgram] align when run with the given [performanceProfile].
 */
fun <Result> assertAligned(
    performanceProfile: PerformanceProfile,
    firstProgram: Aggregate<Int>.() -> Result,
    secondProgram: Aggregate<Int>.() -> Result,
) {
    val first = getSharedDataKeys(performanceProfile, firstProgram)
    val second = getSharedDataKeys(performanceProfile, secondProgram)
    assertEquals(first, second)
}

/**
 * Asserts that the [firstProgram] and [secondProgram] do not align when run with the given [performanceProfile].
 */
fun <First, Second> assertNotAligned(
    performanceProfile: PerformanceProfile,
    firstProgram: Aggregate<Int>.() -> First,
    secondProgram: Aggregate<Int>.() -> Second,
) {
    val first = getSharedDataKeys(performanceProfile, firstProgram)
    val second = getSharedDataKeys(performanceProfile, secondProgram)
    assertNotEquals(first, second)
}
