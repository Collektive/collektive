package it.unibo.collektive

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.GradientStdlib.gradient

class GradientTest : FreeSpec({
    "The gradient should return 0.0 when the source is true" {
        aggregate(0) {
            val distances = exchange(Double.POSITIVE_INFINITY) {
                it.map { 10.0 }
            }
            gradient(true) { distances } shouldBe 0.0
        }
    }
    "The gradient should return Infinity when is not source and there are no other devices" {
        aggregate(0) {
            val distances = exchange(Double.POSITIVE_INFINITY) {
                it.map { 10.0 }
            }
            gradient(false) { distances } shouldBe Double.POSITIVE_INFINITY
        }
    }
})
