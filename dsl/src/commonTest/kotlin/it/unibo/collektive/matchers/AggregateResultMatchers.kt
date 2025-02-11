package it.unibo.collektive.matchers

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.AggregateResult
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.networking.NoNeighborsData
import it.unibo.collektive.path.Path
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Asserts that two aggregate programs align.
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
 * Asserts that two aggregate programs do not align.
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

/**
 * Matcher checking if the result of an aggregate program aligns with the [expected] result.
 *
 * The alignment is checked by comparing the paths of the messages in the result with
 * the paths of the messages in the expected result.
 *
 * Example:
 * ```
 * val first = aggregate(0) { neighboringViaExchange(10) }
 * val result = aggregate(0) { neighboringViaExchange(10) }
 * result should alignWith(first)
 * ```
 */
fun <ID : Any, R> alignWith(expected: AggregateResult<ID, R>) =
    Matcher<AggregateResult<ID, R>> { valueResult ->
        val originPaths =
            valueResult.toSend
                .prepareMessageFor(valueResult.localId)
                .sharedData.keys
        val expectedPaths =
            expected.toSend
                .prepareMessageFor(valueResult.localId)
                .sharedData.keys
        aggregateMatcher(expectedPaths, originPaths)
    }

/**
 * Matcher checking if two aggregate programs align.
 *
 * The alignment is checked by comparing the paths of the messages in the result
 * with the paths of the messages in the [expected] result.
 *
 * Example:
 * ```
 * acProgram { neighboringViaExchange(0) } should alignWith { neighboringViaExchange(0) }
 */
fun <R> alignWith(expected: Aggregate<Int>.() -> R): Matcher<Aggregate<Int>.() -> R> =
    Matcher { program ->
        val expectedRes =
            aggregate(0, emptyMap(), NoNeighborsData(), compute = expected)
                .run { toSend.prepareMessageFor(this.localId).sharedData.keys }
        val result =
            aggregate(0, emptyMap(), NoNeighborsData(), compute = program)
                .run { toSend.prepareMessageFor(this.localId).sharedData.keys }
        aggregateMatcher(expectedRes, result)
    }

private fun aggregateMatcher(
    expected: Set<Path>,
    actual: Set<Path>,
): MatcherResult =
    MatcherResult(
        actual == expected,
        { "Expected the following paths: $expected, but got: $actual" },
        { "Got the following paths: $actual, while expecting: $expected" },
    )
