/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType

/**
 * Checks if the [FirExpression] is structurally equivalent to another [FirExpression].
 */
internal fun FirExpression.isStructurallyEquivalentTo(other: FirExpression): Boolean = render() == other.render()

internal fun FirExpression.returnsAField() = FIELD_CLASS_FQ_NAME == returnType()

internal fun FirExpression.returnType() = resolvedType.classId?.asFqNameString()
