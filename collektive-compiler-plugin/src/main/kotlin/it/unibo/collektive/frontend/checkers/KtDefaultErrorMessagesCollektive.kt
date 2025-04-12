/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.checkers

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers

/**
 * Mapping between the errors and warnings defined in [FirCollektiveErrors] and their respective error messages.
 */
object KtDefaultErrorMessagesCollektive : BaseDiagnosticRendererFactory() {
    @Suppress("ktlint:standard:property-naming")
    override val MAP =
        KtDiagnosticFactoryToRendererMap("Collektive").apply {
            put(
                FirCollektiveErrors.FORBIDDEN_FUNCTION_CALL,
                "The function ''{0}'' should not be called explicitly",
                CommonRenderers.STRING,
            )
            put(
                FirCollektiveErrors.AGGREGATE_FUNCTION_INSIDE_ITERATION,
                """
                Aggregate function ''{0}'' has been called inside a loop construct without explicit alignment.
                The same path may generate interactions more than once, leading to ambiguous alignment.
                
                Consider to wrap the function into the ''alignedOn'' method with a unique element.
                """.trimIndent(),
                CommonRenderers.STRING,
            )
            put(
                FirCollektiveErrors.FUNCTION_WITH_AGGREGATE_PARAMETER_INSIDE_ITERATION,
                """
                Function ''{0}'', that accepts and uses an aggregate argument, has been called inside a loop construct 
                without explicit alignment.
                The same path may generate interactions more than once, leading to ambiguous alignment.
                
                Consider to wrap the function into the ''alignedOn'' method with a unique element, either at the call site
                or inside the ''{0}'' function declaration, wrapping the involved aggregate calls.
                """.trimIndent(),
                CommonRenderers.STRING,
            )
            put(
                FirCollektiveErrors.UNNECESSARY_CONSTRUCT_CALL,
                """
                The ''{0}'' construct may not be necessary for this use case, as the parameter of the provided anonymous 
                function is unused within its body.
                
                Consider using a different construct or eliminating the call altogether.
                """.trimIndent(),
                CommonRenderers.STRING,
            )
            put(
                FirCollektiveErrors.UNNECESSARY_YIELDING_CONTEXT,
                """
                The yielding block inside the ''{0}'' construct may not be necessary for this use case, as the 
                expression that is exchanged is the same as the one yielded inside the ''yielding'' block.
                
                Consider switching to the same construct without the ''yielding'' block.
                """.trimIndent(),
                CommonRenderers.STRING,
            )
            put(
                FirCollektiveErrors.IMPROPER_EVOLVE_CONSTRUCT,
                """
                The ''{0}'' construct can be replaced with the `share` construct call. 
                """.trimIndent(),
                CommonRenderers.STRING,
            )
        }
}
