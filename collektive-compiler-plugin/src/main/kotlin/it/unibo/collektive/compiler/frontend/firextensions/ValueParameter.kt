/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import org.jetbrains.kotlin.fir.declarations.FirValueParameter

/**
 * Checks whether this [FirValueParameter] has a return type of `Field`.
 *
 * @return `true` if the parameter is of type `Field`, `false` otherwise
 */
internal fun FirValueParameter.isField(): Boolean = returnTypeRef.isField()
