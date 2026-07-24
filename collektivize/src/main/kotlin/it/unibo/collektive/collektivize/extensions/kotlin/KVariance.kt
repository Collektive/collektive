/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize.extensions.kotlin

import com.squareup.kotlinpoet.KModifier
import kotlin.reflect.KVariance

internal fun KVariance?.toKModifier() = when (this) {
    null -> null
    KVariance.INVARIANT -> null
    KVariance.IN -> KModifier.IN
    KVariance.OUT -> KModifier.OUT
}
