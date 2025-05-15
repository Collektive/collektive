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
 * Checks whether this [FirExpression] is structurally equivalent to [other].
 *
 * Structural equivalence is determined by comparing the rendered source representations.
 *
 * @param other the expression to compare against
 * @return `true` if the two expressions are structurally identical, `false` otherwise
 */
internal fun FirExpression.isStructurallyEquivalentTo(other: FirExpression): Boolean = render() == other.render()

/**
 * Determines whether this [FirExpression] returns a value of type `Field`.
 *
 * @return `true` if the return type corresponds to `Field`, `false` otherwise
 */
internal fun FirExpression.returnsAField(): Boolean = FIELD_CLASS_FQ_NAME == returnType()

/**
 * Retrieves the fully qualified name of the return type of this [FirExpression].
 *
 * @return the FQ name of the return type, or `null` if unavailable
 */
internal fun FirExpression.returnType(): String? = resolvedType.classId?.asFqNameString()
