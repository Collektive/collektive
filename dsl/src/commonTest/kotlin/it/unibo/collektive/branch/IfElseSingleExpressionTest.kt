package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.aggregate
import it.unibo.collektive.aggregate.neighbouring
import it.unibo.collektive.stack.Path
import it.unibo.collektive.utils.getPaths

class IfElseSingleExpressionTest : StringSpec({
    val id0 = IntId(0)

    "True condition in if else block" {
        val customCondition = true
        val result = aggregate(id0) {
            if (customCondition) neighbouring("test") else neighbouring("test")
        }
        var paths = emptySet<Path>()
        result.toSend.forEach { paths = paths + it.getPaths() }
        paths shouldContain Path(listOf("branch[customCondition, true]", "neighbouring.1", "exchange.1"))
    }

    "False condition in if else block" {
        val customCondition = false
        val result = aggregate(id0) {
            if (customCondition) neighbouring("test") else neighbouring("test")
        }
        var paths = emptySet<Path>()
        result.toSend.forEach { paths = paths + it.getPaths() }
        paths shouldContain Path(listOf("branch[constant, false]", "neighbouring.2", "exchange.1"))
    }
})
