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
 * Checks whether any of the elements in the current [CheckerContext]
 * are annotated to disable the Collektive compiler plugin.
 *
 * This inspects all [FirAnnotation]s in the [containingElements] and returns `true`
 * if any annotation matches [IGNORE_FUNCTION_ANNOTATION_FQ_NAME].
 *
 * @receiver the [CheckerContext] in FIR analysis
 * @return `true` if plugin execution is disabled by annotation; `false` otherwise
 */
internal fun CheckerContext.hasAnnotationDisablingPlugin(): Boolean = containingElements
    .flatMap { (it as? FirAnnotationContainer)?.annotations.orEmpty() }
    .any { it.disablesPlugin() }

/**
 * Determines whether the current context corresponds to a `project` helper call.
 *
 * This checks whether the top-level function in the context matches
 * the Collektive `project(...)` function and has the expected structure.
 *
 * @receiver the [CheckerContext] in FIR analysis
 * @return `true` if this is a call to the `project` function; `false` otherwise
 */
internal fun CheckerContext.isAggregateProjection() = containingElements
    .firstNotNullOfOrNull { it as? FirSimpleFunction }
    ?.run {
        symbol.callableId.asSingleFqName().toString() == CollektiveNames.PROJECTION_FUNCTION_FQ_NAME &&
            valueParameters.size == 1 &&
            valueParameters.first().isField() &&
            returnTypeRef.isField()
    } == true

/**
 * Checks whether the current context is inside a function
 * that operates over aggregate structures.
 *
 * @receiver the [CheckerContext] to inspect
 * @return `true` if inside an aggregate-aware function; `false` otherwise
 */
internal fun CheckerContext.isInsideAggregateFunction(): Boolean =
    containingElements.any { (it as? FirFunction).isAggregate(this) }
