/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import it.unibo.collektive.compiler.common.CollektiveNames.IGNORE_FUNCTION_ANNOTATION_FQ_NAME
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType

/**
 * Checks whether this annotation disables the Collektive compiler plugin for the annotated function.
 *
 * This returns `true` if the annotation matches the fully qualified name defined in
 * [IGNORE_FUNCTION_ANNOTATION_FQ_NAME], indicating that the function should be excluded
 * from aggregate analysis and plugin checks.
 *
 * @receiver the [FirAnnotation] to inspect
 * @return `true` if the annotation disables the plugin, `false` otherwise
 */
internal fun FirAnnotation.disablesPlugin() =
    resolvedType.classId?.asFqNameString() == IGNORE_FUNCTION_ANNOTATION_FQ_NAME
