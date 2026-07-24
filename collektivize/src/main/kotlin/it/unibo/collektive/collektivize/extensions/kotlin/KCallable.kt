/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize.extensions.kotlin

import it.unibo.collektive.collektivize.extensions.java.annotationsFromBytecode
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaMethod

internal fun KCallable<*>.isProperty() = this is KProperty<*>

internal fun KCallable<*>.requiredOptInMarkers(): Set<KClass<out Annotation>> {
    val reflectedMarkers: Sequence<KClass<out Annotation>> = annotations.asSequence().map { it.annotationClass }
    // Kotlin does not allow using @RequiresOptIn as a normal class name. We must go search in the bytecode
    val javaMethod = runCatching { (this as? KFunction<*>)?.javaMethod }.getOrNull()
    val bytecodeMarkers: Sequence<KClass<out Annotation>> = javaMethod?.annotationsFromBytecode().orEmpty()
    return (reflectedMarkers + bytecodeMarkers)
        .filter { it.isErrorLevelOptInMarker() }
        .toSet()
}
