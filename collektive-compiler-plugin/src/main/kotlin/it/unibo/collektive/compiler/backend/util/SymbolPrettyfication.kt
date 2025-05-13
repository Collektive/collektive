/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.backend.util


private val replacements = listOf(
    "it.unibo.alchemist." to "⚗️",
    "it.unibo.collektive.aggregate.api.Aggregate._ serialization aware neighboring" to "↔",
    "it.unibo.collektive.aggregate.api.Aggregate._ serialization aware exchanging" to "🔄",
    "it.unibo.collektive.aggregate.api.Aggregate.InternalAPI" to "🔐",
    "it.unibo.collektive.aggregate.api.DataSharingMethod" to "💾",
    "it.unibo.collektive.field.Field" to "φ",
    "kotlin.Function" to "ƒ_",
)

private val removedPrefixes = listOf(
    "kotlin.",
    "it.unibo.collektive.",
)

/**
 * Improves the readability of a fully qualified name by replacing
 * well-known package prefixes and symbolic names with emojis or shorter symbols.
 *
 * Used mainly to generate compact, user-friendly alignment tokens.
 *
 * @receiver the original fully qualified name
 * @return a compact and symbolic representation
 */
fun String.withBetterSymbols(): String {
    val clean = replacements.fold(this) { current, (replaced, replacement) -> current.replace(replaced, replacement) }
    return when {
        removedPrefixes.any { clean.startsWith(it) } -> clean.substringAfterLast('.')
        else -> clean
    }
}
