/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.common

import it.unibo.collektive.compiler.common.CollektiveNames.AGGREGATE_CLASS_NAME
import it.unibo.collektive.compiler.common.CollektiveNames.ALIGNED_ON_FUNCTION_NAME

/**
 * Contains canonical names for aggregate-related functions, classes, and annotations.
 *
 * This object centralizes string constants representing:
 * - Fully qualified names (FQNs) of DSL functions and types
 * - Function names used by the Collektive Kotlin compiler plugin
 *
 * These constants are used to match IR declarations, resolve symbols, and generate IR.
 */
object CollektiveNames {

    private const val SOURCES_ROOT = "it/unibo/collektive/compiler/sources/"

    private fun regex(regex: String) = Regex(regex, RegexOption.MULTILINE)

    private fun Regex.extractSingleFrom(file: String) = findAll(readSourceFile(file)).single().groupValues[1]

    private fun readSourceFile(name: String): String =
        checkNotNull(this::class.java.classLoader.getResource("$SOURCES_ROOT$name.kt")) {
            "Cannot find resource: $name"
        }.readText()

    private fun extractInterfaceName(fileName: String): String =
        regex("""interface (\w+)""").extractSingleFrom(fileName)

    private fun extractPackageName(fileName: String): String =
        regex("""^package (\w+(?:\.\w+)*)""").extractSingleFrom(fileName)

    private fun extractAnnotationName(from: String): String =
        regex("""^annotation class (\w+)""").extractSingleFrom(from)

    /** Simple name of the [AGGREGATE_CLASS_NAME] interface. Useful for logging and diagnostics. */
    const val AGGREGATE_CLASS_NAME: String = "Aggregate"

    /** The package containing the Aggregate DSL API. */
    val AGGREGATE_API_PACKAGE: String = extractPackageName(AGGREGATE_CLASS_NAME)

    /** Fully qualified name of the [AGGREGATE_CLASS_NAME] interface. */
    val AGGREGATE_CLASS_FQ_NAME: String = "$AGGREGATE_API_PACKAGE.$AGGREGATE_CLASS_NAME"

    /** Name of the top-level alignedOn DSL function. */
    val ALIGNED_ON_FUNCTION_NAME: String =
        regex("""^\s*fun <\w+> (\w+)\(\w+: Any\?, \w+\: \(\) -> \w+\): \w+""")
            .extractSingleFrom(AGGREGATE_CLASS_NAME)

    /** Fully qualified name of the [ALIGNED_ON_FUNCTION_NAME]. */
    val ALIGNED_ON_FUNCTION_FQ_NAME: String = "$AGGREGATE_CLASS_FQ_NAME.$ALIGNED_ON_FUNCTION_NAME"

    /** Name of the alignment function used to begin an alignment block. */
    val ALIGN_FUNCTION_NAME: String = regex("""^\s*fun (\w+)\(\w+\:\sAny\?\)$""")
        .extractSingleFrom(AGGREGATE_CLASS_NAME)

    /** Fully qualified name of the alignment function. */
    val ALIGN_FUNCTION_FQ_NAME: String = "$AGGREGATE_CLASS_FQ_NAME.$ALIGN_FUNCTION_NAME"

    /** The base package for the Collektive DSL API. */
    val COLLEKTIVE_API_PACKAGE: String = AGGREGATE_API_PACKAGE

    /** Name of the dealignment function used to close an alignment block. */
    val DEALIGN_FUNCTION_NAME: String = regex("""^\s*fun (\w+)\(\)""").extractSingleFrom(AGGREGATE_CLASS_NAME)

    /** Fully qualified name of the dealignment function. */
    val DEALIGN_FUNCTION_FQ_NAME: String = "$AGGREGATE_CLASS_FQ_NAME.$DEALIGN_FUNCTION_NAME"

    /** Fully qualified name of the Field class. Used to identify field types in IR. */
    val FIELD_CLASS_FQ_NAME: String = "${extractPackageName("Field")}.${extractInterfaceName("Field")}"

    /** Fully qualified name of the evolve function. */
    val EVOLVE_FUNCTION_FQ_NAME: String = "$AGGREGATE_CLASS_FQ_NAME.evolve"

    /** Fully qualified name of the evolving function. */
    val EVOLVING_FUNCTION_FQ_NAME: String = "$AGGREGATE_CLASS_FQ_NAME.evolving"

    /** Fully qualified name of the `exchange(...)` function. */
    val EXCHANGE_FUNCTION_FQ_NAME: String = "$COLLEKTIVE_API_PACKAGE.exchange"

    /** Fully qualified name of the `exchanging(...)` function. */
    val EXCHANGING_FUNCTION_FQ_NAME: String = "$COLLEKTIVE_API_PACKAGE.exchanging"

    /** Fully qualified name of the `neighboring(...)` function for building neighbor views. */
    val NEIGHBORING_FUNCTION_FQ_NAME: String = "$COLLEKTIVE_API_PACKAGE.neighboring"

    /** Fully qualified name of the ignore annotation used to suppress automatic alignment. */
    val IGNORE_FUNCTION_ANNOTATION_FQ_NAME: String =
        "${extractPackageName("CollektiveIgnore")}.${extractAnnotationName("CollektiveIgnore")}"

    /** Name of the `project(...)` function for field projections. */
    const val PROJECTION_FUNCTION_NAME: String = "project"

    /** Fully qualified name of the `project(...)` function for field projections. */
    val PROJECTION_FUNCTION_FQ_NAME: String = "$COLLEKTIVE_API_PACKAGE.impl.$PROJECTION_FUNCTION_NAME"

    /** Fully qualified name of the `share(...)` function. */
    val SHARE_FUNCTION_FQ_NAME: String = "$COLLEKTIVE_API_PACKAGE.share"

    /** Fully qualified name of the `sharing(...)` function for declarative data flow. */
    val SHARING_FUNCTION_FQ_NAME: String = "$COLLEKTIVE_API_PACKAGE.sharing"

    /** Fully qualified name of the yielding function. */
    val YIELDING_FUNCTION_FQ_NAME: String = extractPackageName("YieldSupport").let { pkg ->
        val funName = regex("""fun \w+\.(\w+)\(\w+\: \w+\.\(\) -> \w+\): \w+""")
            .extractSingleFrom("YieldSupport")
        "$pkg.YieldingContext.$funName"
    }
}
