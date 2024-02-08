package it.unibo.collektive.matchers

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.AggregateResult
import it.unibo.collektive.aggregate.api.Aggregate

fun <ID : Any, R> alignWith(expected: AggregateResult<ID, R>) = Matcher<AggregateResult<ID, R>> { valueResult ->
    val originPaths = valueResult.toSend.messages.keys
    val expectedPaths = expected.toSend.messages.keys
    MatcherResult(
        originPaths == expectedPaths,
        { "Expected the following paths: $expectedPaths, but got: $originPaths" },
        { "Got the following paths: $originPaths, while expecting: $expectedPaths" },
    )
}

fun <R> alignWith(expect: Aggregate<Int>.() -> R): Matcher<Aggregate<Int>.() -> R> = Matcher { program ->
    val expected = aggregate(0, emptySet(), emptyMap(), expect).run { toSend.messages.keys }
    val result = aggregate(0, emptySet(), emptyMap(), program).run { toSend.messages.keys }
    MatcherResult(
        result == expect,
        { "Expected the following paths: $expected, but got: $result" },
        { "Got the following paths: $result, while expecting: $expected" },
    )
}

fun <R> acProgram(acProgram: Aggregate<Int>.() -> R): Aggregate<Int>.() -> R = acProgram
