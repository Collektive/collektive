/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend

import it.unibo.collektive.compiler.common.CollektiveNames
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers

/**
 * Mapping between the errors and warnings defined in [CollektiveFrontendErrors]
 * and their respective error messages.
 */
object CollektiveFrontendErrorMessageRenderer : BaseDiagnosticRendererFactory() {
    @Suppress("ktlint:standard:property-naming")
    override val MAP =
        KtDiagnosticFactoryToRendererMap("Collektive").apply {
            put(
                CollektiveFrontendErrors.AGGREGATE_FUNCTION_INSIDE_ITERATION,
                """
                Aggregate function ''{0}'' is called inside a loop without explicit alignment.  
                This may cause the same execution path to trigger multiple interactions, resulting in ambiguous alignment.
                Wrap the function call using ''alignedOn'' with a unique key to ensure consistent alignment.
                """.trimIndent(),
                CommonRenderers.STRING,
            )
            put(
                CollektiveFrontendErrors.BRANCH_RETURNS_FIELD,
                """
                    This branch returns a ''{0}'', which is an instance of ${CollektiveNames.FIELD_CLASS_FQ_NAME}.
                    Returning fields from branches is disallowed, as it may break alignment: fields are projected within branches  
                    and may be restricted to partial domains.
                    Build the field before the branch and map it afterward.  
                    For example, transform:
                        if (X) neighboring(Y) else neighboring(Z)
                    into:
                       neighboring(Y).alignedMapValues(neighboring(Z)) '{' _, y, z -> if (X) y else z '}'.
                """.trimIndent(),
                CommonRenderers.STRING,
            )
            put(
                CollektiveFrontendErrors.FORBIDDEN_FUNCTION_CALL,
                "The function ''{0}'' must not be called explicitly",
                CommonRenderers.STRING,
            )
            put(
                CollektiveFrontendErrors.IMPROPER_EVOLVE_CONSTRUCT,
                """
                This ''{0}'' can be replaced with ''share'' for better performance.
                """.trimIndent(),
                CommonRenderers.STRING,
            )
            put(
                CollektiveFrontendErrors.UNNECESSARY_CONSTRUCT_CALL,
                """
                This ''{0}'' appears unnecessary in this case, as the anonymous function''s parameter is unused.
                Consider using a different construct or eliminating the call altogether.
                """.trimIndent(),
                CommonRenderers.STRING,
            )
            put(
                CollektiveFrontendErrors.UNNECESSARY_YIELDING_CONTEXT,
                """
                The ''yielding'' block inside ''{0}'' may be redundant, as the yielded expression is identical to the one being exchanged.
                Consider using the non-ing (non-yielding) version of the same construct (e.g., ''share'' instead of ''sharing'').
                """.trimIndent(),
                CommonRenderers.STRING,
            )
        }
}
