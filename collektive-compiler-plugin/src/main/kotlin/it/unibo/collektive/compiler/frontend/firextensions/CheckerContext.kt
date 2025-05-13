/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import it.unibo.collektive.compiler.common.CollektiveNames
import it.unibo.collektive.compiler.common.CollektiveNames.IGNORE_FUNCTION_ANNOTATION_FQ_NAME
import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.FirAnnotation

/**
 * Checks whether any of the elements in the current [CheckerContext] contain an annotation
 * that disables the Collektive compiler plugin.
 *
 * This function inspects all [FirAnnotation]s in the [containingElements] and returns `true`
 * if any of them match the disabling annotation defined by [IGNORE_FUNCTION_ANNOTATION_FQ_NAME].
 *
 * @receiver the current [CheckerContext] during FIR analysis
 * @return `true` if plugin execution should be disabled due to annotations, `false` otherwise
 */
internal fun CheckerContext.hasAnnotationDisablingPlugin(): Boolean = containingElements
    .flatMap { (it as? FirAnnotationContainer)?.annotations.orEmpty() }
    .any { it.disablesPlugin() }

internal fun CheckerContext.isAggregateProjection() = containingElements
    .firstNotNullOfOrNull { it as? FirSimpleFunction }
    ?.run {
        symbol.callableId.asSingleFqName().toString() == CollektiveNames.PROJECTION_FUNCTION_FQ_NAME &&
            this.valueParameters.size == 1 &&
            valueParameters.first().isField() &&
            returnTypeRef.isField()
    } == true

/**
 * Checks whether the current context is enclosed within an aggregate function.
 *
 * @receiver the [CheckerContext] to inspect
 * @return `true` if inside a function whose receiver or parameters are aggregate-typed
 */
internal fun CheckerContext.isInsideAggregateFunction(): Boolean =
    containingElements.any { (it as? FirFunction).isAggregate(this) }
