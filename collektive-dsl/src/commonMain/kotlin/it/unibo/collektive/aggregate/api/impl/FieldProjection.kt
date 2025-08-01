/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api.impl

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.CollektiveIgnore
import it.unibo.collektive.aggregate.api.DelicateCollektiveApi
import it.unibo.collektive.aggregate.api.neighborhood

/**
 * Projects the field into the current context, restricting the field to the current context.
 *
 * A field may be misaligned if captured by a sub-scope which contains an alignment operation.
 * This function takes such [field] and restricts it to be aligned with the current neighbors.
 *
 * This method is meant to be used internally by the Collektive compiler plugin
 * and should never be called from the outside.
 *
 * If you happen to call it, be aware that you are probably building a terrible kludge that will
 * break sooner or later.
 *
 */
@CollektiveIgnore(
    """
        Projecting the projection function would inevitably lead to an infinite loop.
        """,
)
@DelicateCollektiveApi
fun <ID : Any, T> project(field: Field<ID, T>): Field<ID, T> {
    // Manually-aligned.
    // Equivalent to `mapNeighborhood { field[it] }`,
    // but avoids the operation entirely if there has been no restriction.
    field.context.align("<field-projection>")
    try {
        val others = field.context.neighborhood()
        return when {
            field.neighborsCount == others.neighborsCount -> field
            field.neighborsCount > others.neighborsCount -> others.map { (id, _) -> field[id] }
            else -> error(
                """
            Collektive is in an inconsistent state, this is most likely a bug in the implementation.
            Field $field with ${field.neighborsCount} neighbors has been projected into a context
            with more neighbors, ${others.neighborsCount}: ${others.neighbors}.
                """.trimIndent().replace(Regex("'\\R"), " "),
            )
        }
    } finally {
        field.context.dealign()
    }
}
