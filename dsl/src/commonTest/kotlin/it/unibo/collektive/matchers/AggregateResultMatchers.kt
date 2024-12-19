package it.unibo.collektive.matchers

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.AggregateResult
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.path.Path

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
        val originPaths = valueResult.toSend.messagesFor(valueResult.localId).keys
        val expectedPaths = expected.toSend.messagesFor(valueResult.localId).keys
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
            aggregate(0, emptyMap(), emptySet(), expected)
                .run { toSend.messagesFor(this.localId).keys }
        val result =
            aggregate(0, emptyMap(), emptySet(), program)
                .run { toSend.messagesFor(this.localId).keys }
        aggregateMatcher(expectedRes, result)
    }

/**
 * Utility test function to create an [aggregateProgram] on fly.
 *
 * Can be combined with [alignWith] to check if two aggregate programs align.
 *
 * Example:
 * ```
 * acProgram { neighboringViaExchange(0) } should alignWith { neighboringViaExchange(0) }
 * ```
 */
fun <R> acProgram(aggregateProgram: Aggregate<Int>.() -> R): Aggregate<Int>.() -> R = aggregateProgram

private fun aggregateMatcher(
    expected: Set<Path>,
    actual: Set<Path>,
): MatcherResult =
    MatcherResult(
        actual == expected,
        { "Expected the following paths: $expected, but got: $actual" },
        { "Got the following paths: $actual, while expecting: $expected" },
    )
