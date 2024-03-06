package it.unibo.collektive.codgen.matcher

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import it.unibo.collektive.compiler.CollektiveJVMCompiler

fun compile() = Matcher<String> {
    val result = CollektiveJVMCompiler.compileString(it)
    MatcherResult(
        result != null,
        { "The code: $it should compile" },
        { "The code: $it should not compile" },
    )
}

fun String.shouldCompile() {
    this should compile()
}

fun String.shouldNotCompile() {
    this shouldNot compile()
}
