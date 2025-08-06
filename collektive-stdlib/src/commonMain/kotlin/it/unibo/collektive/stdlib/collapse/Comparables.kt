/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.collapse

import arrow.core.identity
import it.unibo.collektive.aggregate.Collapse
import it.unibo.collektive.aggregate.CollapsePeers
import it.unibo.collektive.aggregate.CollapseWithSelf

/**
 * Returns the largest element in the collapsed field, including the local value.
 *
 * If multiple elements are tied for the maximum, one is returned arbitrarily.
 *
 * @return the maximal element present in the field.
 */
fun <T : Comparable<T>> CollapseWithSelf<T>.max(): T = maxBy(::identity)

/**
 * Returns the largest peer element in the collapsed field, excluding the local value.
 *
 * If multiple peers are tied for the maximum, one is returned arbitrarily.
 * If there are no peer elements, returns `null`.
 *
 * @return the maximal peer element, or `null` if none exist.
 */
fun <T : Comparable<T>> CollapsePeers<T>.max(): T? = maxBy(::identity)

/**
 * Returns the smallest element in the collapsed field, including the local value.
 *
 * If multiple elements are tied for the minimum, one is returned arbitrarily.
 *
 * @return the minimal element present in the field.
 */
fun <T : Comparable<T>> CollapseWithSelf<T>.min(): T = minBy(::identity)

/**
 * Returns the smallest peer element in the collapsed field, excluding the local value.
 *
 * If multiple peers are tied for the minimum, one is returned arbitrarily.
 * If there are no peer elements, returns `null`.
 *
 * @return the minimal peer element, or `null` if none exist.
 */
fun <T : Comparable<T>> CollapsePeers<T>.min(): T? = minBy(::identity)

/**
 * Returns the maximum between all values in the collapsed field and the provided [base].
 *
 * If the field is empty, returns [base]. Otherwise, performs a fold using [maxOf] so that the result
 * is the greatest element among the field values and [base].
 *
 * @param base the fallback value used when the field is empty and also considered in the comparison.
 * @return the maximal value among field entries and [base].
 */
fun <T : Comparable<T>> Collapse<T>.max(base: T): T = fold(base, ::maxOf)

/**
 * Returns the minimum between all values in the collapsed field and the provided [base].
 *
 * If the field is empty, returns [base]. Otherwise, performs a fold using [minOf] so that the result
 * is the smallest element among the field values and [base].
 *
 * @param base the fallback value used when the field is empty and also considered in the comparison.
 * @return the minimal value among field entries and [base].
 */
fun <T : Comparable<T>> Collapse<T>.min(base: T): T = fold(base, ::minOf)
