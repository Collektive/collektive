/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import it.unibo.collektive.compiler.common.CollektiveNames.AGGREGATE_CLASS_FQ_NAME
import it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.classId

private val aggregateTypes = listOf(AGGREGATE_CLASS_FQ_NAME, FIELD_CLASS_FQ_NAME)

/**
 * Recursively retrieves all super types of this [ConeKotlinType] in the context of the given [session].
 *
 * @receiver a [ConeKotlinType] to analyse
 * @param session the current [FirSession]
 * @return a set of all super types of this type
 */
internal fun ConeKotlinType.allSuperTypes(session: FirSession): Set<ConeKotlinType> {
    val superTypes = directSuperTypes(session)
    return superTypes + superTypes.flatMap { it.allSuperTypes(session) }
}

/**
 * Retrieves the super types of this [ConeKotlinType] in the context of the given [session].
 */
internal fun ConeKotlinType.directSuperTypes(session: FirSession): Set<ConeKotlinType> =
    (toSymbol(session) as? FirClassSymbol)?.resolvedSuperTypes?.toSet().orEmpty()

/**
 * Checks whether the receiver type corresponds to a recognized aggregate type.
 *
 * @receiver a [ConeKotlinType] to analyse
 * @return `true` if the type is an aggregate or field type, `false` otherwise
 */
internal fun ConeKotlinType.isAggregate(session: FirSession): Boolean =
    this.classId?.asFqNameString() in aggregateTypes || directSuperTypes(session).any { it.isAggregate(session) }

/**
 * Checks if any of the types in the sequence represents an aggregate type.
 *
 * @return `true` if at least one type in the sequence is an aggregate type, `false` otherwise.
 */
internal fun Sequence<ConeKotlinType>.anyIsAggregate(session: FirSession): Boolean = any { it.isAggregate(session) }
