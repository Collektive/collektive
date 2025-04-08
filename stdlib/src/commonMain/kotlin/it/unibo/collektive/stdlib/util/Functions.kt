/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.util

/**
 * A function that takes two arguments of the same type and returns a value of the same type.
 */
typealias Accumulator<T> = (T, T) -> T

/**
 * A function that takes two arguments and returns a boolean value.
 */
typealias BiPredicate<X, Y> = (X, Y) -> Boolean

/**
 * A function that takes three arguments of different types and returns a value of the first type.
 */
typealias BiReducer<Destination, X, Y> = (Destination, X, Y) -> Destination

/**
 * A function that takes two arguments of different types and returns a value of the first type.
 */
typealias Reducer<Destination, T> = (Destination, T) -> Destination
