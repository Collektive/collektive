package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.aggregate
import it.unibo.collektive.aggregate.ops.neighbouring
import it.unibo.collektive.stack.Path
import it.unibo.collektive.utils.getPaths

class WhenTest : StringSpec({
    val id0 = IntId(0)

    "When in single expression" {
        val condition = true
        val x = if (condition) "hello" else 123
        val result = aggregate(id0) {
            when (x) {
                is String -> neighbouring("string")
                else -> neighbouring("test")
            }
        }
        var paths = emptySet<Path>()
        result.toSend.forEach { paths = paths + it.getPaths() }
        paths.toString() shouldContain "INSTANCEOF"
        paths.toString() shouldContain "String"
        paths.toString() shouldContain "true"
    }

    "When in single expression in else case" {
        val condition = false
        val x = if (condition) "hello" else 123
        val result = aggregate(id0) {
            when (x) {
                is String -> neighbouring("string")
                else -> neighbouring("test")
            }
        }
        var paths = emptySet<Path>()
        result.toSend.forEach { paths = paths + it.getPaths() }
        paths.toString() shouldContain "false"
    }

    "When with nested function" {
        val condition = true
        val x = if (condition) "hello" else 123
        val result = aggregate(id0) {
            fun test() {
                neighbouring("test")
            }

            fun test2() {
                neighbouring("test2")
            }
            when (x) {
                is String -> test2()
                else -> test()
            }
        }
        var paths = emptySet<Path>()
        result.toSend.forEach { paths = paths + it.getPaths() }
        paths.toString() shouldContain "INSTANCEOF"
        paths.toString() shouldContain "String"
        paths.toString() shouldContain "test"
        paths.toString() shouldContain "test2"
        paths.toString() shouldContain "true"
    }
})
