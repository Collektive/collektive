/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize

import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import it.unibo.collektive.aggregate.Field

/**
 * Collektive Field interface name.
 */
internal val FIELD_INTERFACE = Field::class.asClassName()

/**
 * Package hosting the top-level [Field] extension functions (e.g., `mapValues`, `alignedMapValues`).
 */
internal val FIELD_EXTENSIONS_PACKAGE = "${FIELD_INTERFACE.packageName}.api.field"

/**
 * The `alignedMapValues` [Field] extension function.
 */
internal val ALIGNED_MAP_VALUES = MemberName(FIELD_EXTENSIONS_PACKAGE, "alignedMapValues", isExtension = true)

/**
 * The `mapValues` [Field] extension function.
 */
internal val FIELD_MAP = MemberName(FIELD_EXTENSIONS_PACKAGE, "mapValues", isExtension = true)

/**
 * [Field] extension functions used in the generated code.
 * If a generated member has the same name, they must be imported with an alias,
 * as members of the generated container object would shadow them.
 */
internal val FIELD_EXTENSIONS_USED_IN_GENERATED_CODE = listOf(ALIGNED_MAP_VALUES, FIELD_MAP)

/**
 * Generic ID type variable, `ID : Any`.
 */
internal val ID_BOUNDED_TYPE = TypeVariableName("ID", Any::class.asTypeName())
