package it.unibo.collektive.matchers

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.AggregateResult
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.path.Path

fun <ID : Any, R> alignWith(expected: AggregateResult<ID, R>) = Matcher<AggregateResult<ID, R>> { valueResult ->
    val originPaths = valueResult.toSend.messages.keys
    val expectedPaths = expected.toSend.messages.keys
    aggregateMatcher(expectedPaths, originPaths)
}

fun <R> alignWith(expect: Aggregate<Int>.() -> R): Matcher<Aggregate<Int>.() -> R> = Matcher { program ->
    val expected = aggregate(0, emptySet(), emptyMap(), expect).run { toSend.messages.keys }
    val result = aggregate(0, emptySet(), emptyMap(), program).run { toSend.messages.keys }
    aggregateMatcher(expected, result)
}

fun <R> acProgram(acProgram: Aggregate<Int>.() -> R): Aggregate<Int>.() -> R = acProgram

private fun aggregateMatcher(expected: Set<Path>, actual: Set<Path>): MatcherResult {
    return MatcherResult(
        actual == expected,
        { "Expected the following paths: $expected, but got: $actual" },
        { "Got the following paths: $actual, while expecting: $expected" },
    )
}
