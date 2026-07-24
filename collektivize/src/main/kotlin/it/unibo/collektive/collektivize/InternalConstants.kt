/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize

import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import it.unibo.collektive.aggregate.Field

/**
 * Collektive Field interface name.
 */
internal val FIELD_INTERFACE = Field::class.asClassName()

/**
 * Generic ID type variable, `ID : Any`.
 */
internal val ID_BOUNDED_TYPE = TypeVariableName("ID", Any::class.asTypeName())
