package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathSummary
import it.unibo.collektive.path.impl.IdentityPathSummary

class IfElseSingleExpressionTest : StringSpec({
    val id0 = 0
    val pathRepresentation: (Path) -> PathSummary = { IdentityPathSummary(it) }

    "True condition in if else block" {
        val customCondition = true
        val result = aggregate(id0, pathRepresentation) {
            if (customCondition) neighboringViaExchange("test-true") else neighboringViaExchange("test-false")
        }
        result.toSend.messages.keys.size shouldBe 1
        result.toSend.messages.values.map { it.default } shouldContain "test-true"
    }

    "False condition in if else block" {
        val customCondition = false
        val result = aggregate(id0, pathRepresentation) {
            if (customCondition) neighboringViaExchange("test-true") else neighboringViaExchange("test-false")
        }
        result.toSend.messages.keys.size shouldBe 1
        result.toSend.messages.values.map { it.default } shouldContain "test-false"
    }
})
