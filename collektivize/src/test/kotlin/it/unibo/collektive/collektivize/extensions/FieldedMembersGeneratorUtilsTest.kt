/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

@file:OptIn(DefaultErrorOptIn::class, ErrorOptIn::class, AnotherErrorOptIn::class, WarningOptIn::class)

package it.unibo.collektive.collektivize.extensions

import it.unibo.collektive.collektivize.generatePrimitivesFile
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ErrorOptIn

@RequiresOptIn
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class DefaultErrorOptIn

@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class AnotherErrorOptIn

@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class WarningOptIn

@ErrorOptIn
fun String.errorOpted(): String = this

@DefaultErrorOptIn
fun String.defaultErrorOpted(): String = this

@ErrorOptIn
@WarningOptIn
fun String.errorAndWarningOpted(): String = this

@ErrorOptIn
@AnotherErrorOptIn
fun String.multipleErrorOpted(): String = this

@WarningOptIn
fun String.warningOpted(): String = this

fun String.notOpted(): String = this

class FieldedMembersGeneratorUtilsTest {
    @Test
    fun `plain requires opt in is propagated as error`() {
        val generated = generatePrimitivesFile(
            listOf(String::defaultErrorOpted),
            "generated",
            "Generated",
        )!!.toString()
        assertContains(generated, "@OptIn(DefaultErrorOptIn::class)")
    }

    @Test
    fun `error opt in is propagated to generated function`() {
        val generated = generatePrimitivesFile(
            listOf(String::errorOpted),
            "generated",
            "Generated",
        )!!.toString()
        assertContains(generated, "@OptIn(ErrorOptIn::class)")
    }

    @Test
    fun `warning opt in is ignored when combined with an error opt in`() {
        val generated = generatePrimitivesFile(
            listOf(String::errorAndWarningOpted),
            "generated",
            "Generated",
        )!!.toString()
        assertContains(generated, "@OptIn(ErrorOptIn::class)")
        assertFalse(generated.contains("WarningOptIn::class"))
    }

    @Test
    fun `multiple error opt ins are emitted in one annotation`() {
        val generated = generatePrimitivesFile(
            listOf(String::multipleErrorOpted),
            "generated",
            "Generated",
        )!!.toString()
        assertContains(generated, "@OptIn(ErrorOptIn::class, AnotherErrorOptIn::class)")
    }

    @Test
    fun `warning opt in is not propagated`() {
        val generated = generatePrimitivesFile(listOf(String::warningOpted), "generated", "Generated")!!.toString()
        assertFalse(generated.contains("@OptIn"))
    }

    @Test
    fun `unannotated function is not opted in`() {
        val generated = generatePrimitivesFile(listOf(String::notOpted), "generated", "Generated")!!.toString()
        assertFalse(generated.contains("@OptIn"))
    }
}
