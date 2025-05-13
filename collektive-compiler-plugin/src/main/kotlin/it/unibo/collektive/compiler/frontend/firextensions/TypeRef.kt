/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType

/**
 * Checks whether this [FirTypeRef] represents a `Field` type.
 *
 * @return `true` if the type is `Field`, `false` otherwise
 */
internal fun FirTypeRef.isField(): Boolean = FIELD_CLASS_FQ_NAME == coneType.classId?.asFqNameString()
