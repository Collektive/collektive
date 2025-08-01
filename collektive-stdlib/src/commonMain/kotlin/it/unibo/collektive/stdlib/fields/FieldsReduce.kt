/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.fields

import it.unibo.collektive.aggregate.CollapsePeers
import it.unibo.collektive.aggregate.CollapseWithSelf
import it.unibo.collektive.stdlib.util.Reducer

/**
 * Reduces the elements in this collapse (which includes the local element and peers) into a single value
 * by repeatedly applying [reducer].
 *
 * Because the local element is always present in a CollapseWithSelf, this will always return a value.
 *
 * @param reducer a binary operation that combines two values of type T into one.
 * @return the accumulated result of reducing all elements.
 */
inline fun <T> CollapseWithSelf<T>.reduce(crossinline reducer: Reducer<T>): T = sequence.reduce(reducer)

/**
 * Reduces the elements in this collapse (which excludes the local element, i.e., only peers) into a single value
 * by repeatedly applying [reducer].
 *
 * If there are no peer elements, returns `null`. Otherwise, behaves like a standard reduction over the peer sequence.
 *
 * @param reducer a binary operation that combines two values of type T into one.
 * @return the accumulated result of reducing the peer values, or `null` if the collapse is empty.
 */
inline fun <T> CollapsePeers<T>.reduce(crossinline reducer: Reducer<T>): T? = sequence.reduceOrNull(reducer)
