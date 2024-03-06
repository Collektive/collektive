package it.unibo.collektive.codgen

import com.squareup.kotlinpoet.FileSpec

/**
 * TODO.
 */
fun interface CollektiveCodeGenerator<in Input> {
    /**
     * TODO.
     */
    fun generate(input: Input, packageName: String, fileName: String): FileSpec
}
