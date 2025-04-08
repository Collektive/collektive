/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.consensus

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.sharing
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.stdlib.util.Accumulator
import it.unibo.collektive.stdlib.util.hops
import it.unibo.collektive.stdlib.util.nonOverflowingPlus
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmOverloads

/**
 * Bounded election is a self-stabilizing and priority-based multi-leader election algorithm
 * presented in a [IEEE ACSOS 2022 paper](https://doi.org/10.1109/ACSOS55765.2022.00026).
 *
 * [strength] defines the priority of the candidate.
 * [bound] restricts how far the local candidacy can extend.
 * [metric] is a field that defines the [Distance] between devices.
 * [bottom] is the initial [Distance] of the local candidacy.
 * [accumulateDistance] defines how [Distance]s are accumulated. The triangular inequality must hold.
 * Provided two candidates, [selectBest] selects the best [Candidacy], defaulting to [maxOf]
 */
@JvmOverloads
inline fun <reified ID, reified Distance, reified Strength> Aggregate<ID>.boundedElection(
    strength: Strength,
    bound: Distance,
    metric: Field<ID, Distance>,
    bottom: Distance,
    crossinline accumulateDistance: Accumulator<Distance>,
    crossinline selectBest: Accumulator<Candidacy<ID, Distance, Strength>> = ::maxOf,
): ID where ID : Any, Distance : Comparable<Distance>, Strength : Comparable<Strength> {
    val local = Candidacy(localId, bottom, strength)
    return sharing(local) { candidacies ->
        val localCandidates = candidacies.alignedMap(metric) { candidacy, distance ->
            // Update the distance of the candidacies
            candidacy.copy(distance = accumulateDistance(candidacy.distance, distance))
        }
        localCandidates.fold(local) { current, next ->
            when {
                next.candidate == localId -> current // I have better information about myself
                next.distance > bound -> current // This candidate is past the bound
                else -> selectBest(current, next) // Competition
            }
        }.yielding {
            candidate
        }
    }
}

/**
 * Bounded election is a self-stabilizing and priority-based multi-leader election algorithm
 * presented in a [IEEE ACSOS 2022 paper](https://doi.org/10.1109/ACSOS55765.2022.00026).
 *
 * This version uses the local [ID] as strength.
 *
 * [bound] restricts how far the local candidacy can extend.
 * [metric] is a field that defines the [Distance] between devices.
 * [bottom] is the initial [Distance] of the local candidacy.
 * [accumulateDistance] defines how [Distance]s are accumulated. The triangular inequality must hold.
 * Provided two candidates, [selectBest] selects the best [Candidacy], defaulting to [maxOf]
 */
@JvmOverloads
inline fun <reified ID, reified Distance> Aggregate<ID>.boundedElection(
    bound: Distance,
    metric: Field<ID, Distance>,
    bottom: Distance,
    crossinline accumulateDistance: Accumulator<Distance>,
    crossinline selectBest: Accumulator<Candidacy<ID, Distance, ID>> = ::maxOf,
): ID where ID : Comparable<ID>, Distance : Comparable<Distance> =
    boundedElection(localId, bound, metric, bottom, accumulateDistance, selectBest)

/**
 * Bounded election is a self-stabilizing and priority-based multi-leader election algorithm
 * presented in a [IEEE ACSOS 2022 paper](https://doi.org/10.1109/ACSOS55765.2022.00026).
 *
 * [strength] defines the priority of the candidate.
 * [bound] restricts how far the local candidacy can extend.
 * [metric] is a field that defines the distance between devices.
 * Provided two candidates, [selectBest] selects the best [Candidacy], defaulting to [maxOf]
 */
inline fun <reified ID, reified Strength> Aggregate<ID>.boundedElection(
    strength: Strength,
    bound: Double,
    metric: Field<ID, Double>,
    crossinline selectBest: Accumulator<Candidacy<ID, Double, Strength>> = ::maxOf,
): ID where ID : Any, Strength : Comparable<Strength> =
    boundedElection<ID, Double, Strength>(strength, bound, metric, 0.0, Double::plus, selectBest)

/**
 * Bounded election is a self-stabilizing and priority-based multi-leader election algorithm
 * presented in a [IEEE ACSOS 2022 paper](https://doi.org/10.1109/ACSOS55765.2022.00026).
 *
 * [strength] defines the priority of the candidate.
 * [bound] restricts how far the local candidacy can extend.
 * [metric] is a field that defines the distance between devices.
 * Provided two candidates, [selectBest] selects the best [Candidacy], defaulting to [maxOf]
 */
@JvmOverloads
inline fun <reified ID, reified Strength> Aggregate<ID>.boundedElection(
    strength: Strength,
    bound: Int,
    metric: Field<ID, Int> = hops(),
    crossinline selectBest: Accumulator<Candidacy<ID, Int, Strength>> = ::maxOf,
): ID where ID : Any, Strength : Comparable<Strength> =
    boundedElection<ID, Int, Strength>(strength, bound, metric, 0, Int::nonOverflowingPlus, selectBest)

/**
 * Bounded election is a self-stabilizing and priority-based multi-leader election algorithm
 * presented in a [IEEE ACSOS 2022 paper](https://doi.org/10.1109/ACSOS55765.2022.00026).
 *
 * This version uses the local [ID] as strength.
 *
 * [bound] restricts how far the local candidacy can extend.
 * [metric] is a field that defines the distance between devices.
 * Provided two candidates, [selectBest] selects the best [Candidacy], defaulting to [maxOf]
 */
inline fun <reified ID> Aggregate<ID>.boundedElection(
    bound: Double,
    metric: Field<ID, Double>,
    crossinline selectBest: Accumulator<Candidacy<ID, Double, ID>> = ::maxOf,
): ID where ID : Comparable<ID> = boundedElection(localId, bound, metric, selectBest)

/**
 * Bounded election is a self-stabilizing and priority-based multi-leader election algorithm
 * presented in a [IEEE ACSOS 2022 paper](https://doi.org/10.1109/ACSOS55765.2022.00026).
 *
 * This version uses the local [ID] as strength.
 *
 * [bound] restricts how far the local candidacy can extend.
 * [metric] is a field that defines the distance between devices.
 * Provided two candidates, [selectBest] selects the best [Candidacy], defaulting to [maxOf]
 */
@JvmOverloads
inline fun <reified ID> Aggregate<ID>.boundedElection(
    bound: Int,
    metric: Field<ID, Int> = hops(),
    crossinline selectBest: Accumulator<Candidacy<ID, Int, ID>> = ::maxOf,
): ID where ID : Comparable<ID> = boundedElection(localId, bound, metric, selectBest)

/**
 * Self-stabilizing, priority-based single-leader election
 * built by forcing [Int.MAX_VALUE] as bound for [boundedElection].
 *
 * [strength] defines the priority of the candidate.
 * [metric] is a field that defines the distance between devices.
 * Provided two candidates, [selectBest] selects the best [Candidacy], defaulting to [maxOf]
 */
@JvmOverloads
inline fun <reified ID, reified Strength> Aggregate<ID>.globalElection(
    strength: Strength,
    metric: Field<ID, Int> = hops(),
    crossinline selectBest: Accumulator<Candidacy<ID, Int, Strength>> = ::maxOf,
): ID where ID : Any, Strength : Comparable<Strength> = boundedElection(strength, Int.MAX_VALUE, metric, selectBest)

/**
 * Self-stabilizing, priority-based single-leader election
 * built by forcing [Int.MAX_VALUE] as bound for [boundedElection].
 *
 * This version uses the local [ID] as strength.
 */
inline fun <reified ID : Comparable<ID>> Aggregate<ID>.globalElection(): ID = globalElection(localId)

/**
 * A [Candidacy] represents a candidate in an election.
 * [candidate] is the [ID] of the candidate.
 * [distance] is the [Distance] of the current device from the [candidate].
 * [strength] is the [Strength] of the candidate.
 */
@Serializable
data class Candidacy<ID, Distance, Strength>(val candidate: ID, val distance: Distance, val strength: Strength) :
    Comparable<Candidacy<ID, Distance, Strength>>
    where ID : Any, Distance : Comparable<Distance>, Strength : Comparable<Strength> {

    override fun compareTo(other: Candidacy<ID, Distance, Strength>): Int =
        compareBy<Candidacy<ID, Distance, Strength>> { it.strength }
            .thenByDescending { it.distance }
            .compare(this, other)
}
