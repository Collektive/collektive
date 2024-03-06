package it.unibo.collektive.codgen

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.codgen.matcher.shouldCompile

class GeneratedSourceValidityTest : FreeSpec({
    "The generated source should compile correctly" {
        """
            package it.unibo.collektive.codgen
            fun foo() = "bar"
        """.trimIndent().shouldCompile()
    }
})
