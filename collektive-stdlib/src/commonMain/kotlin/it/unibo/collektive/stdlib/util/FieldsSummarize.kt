/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.util

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.FieldEntry
import it.unibo.collektive.aggregate.toFieldEntry
import it.unibo.collektive.stdlib.fields.max

sealed interface Summarize<E> {
    val entries: Sequence<E>
}

interface SummarizeWithoutSelf<E> : Summarize<E> {
    val includingSelf: SummarizeWithSelf<E>

    companion object {
        context(target: Field<ID, T>)
        operator fun <ID: Any, T, E> invoke(target: Field<ID, T>, extractSequence: Field<ID, T>.() -> Sequence<E>): SummarizeWithoutSelf<E> = object : SummarizeWithoutSelf<E> {
            override val entries: Sequence<E> by lazy {
                extractSequence().excludeSelf().asSequence()
            }
            override val includingSelf: SummarizeWithSelf<E> get() = object : SummarizeWithSelf<E> {
                override val entries: Sequence<E> = emptySequence()
            }
        }
    }
}

interface SummarizeWithSelf<E> : Summarize<E>

fun <ID: Any, T> Field<ID, T>.summarize(): SummarizeWithoutSelf<FieldEntry<ID, T>> = object : SummarizeWithoutSelf<FieldEntry<ID, T>> {
    override val entries: Sequence<FieldEntry<ID, T>> by lazy {
        this@summarize.excludeSelf().entries.asSequence().map { it.toFieldEntry() }
    }
    override val includingSelf: SummarizeWithSelf<FieldEntry<ID, T>> get() = object : SummarizeWithSelf<ID, T> {
        override val entries: Field<ID, T> = this@summarize
    }
}

//class Fold<ID : Any, T, InputType, Destination>(val destination: Destination, val field: Field<ID, T>) : FieldsSummarize<InputType, Destination, Destination> {
//    override operator fun invoke(reducer: Accumulator<Destination, InputType>): Destination {
//        field.
//    }
//
//}
//class Reduce<ID : Any, T, InputType>(val field: Field<ID, T>) : FieldsSummarize<InputType, InputType, InputType?> {
//    override fun op(reducer: Accumulator<InputType, InputType>): InputType? {
//        field.reduce { }
//    }
//
//}
//
//fun <ID : Any, T, Giorgio> Field<ID, T>.reducing(): Reduce<Giorgio> = excludeSelf().
//fun <ID : Any, T, Giorgio, Destination> Field<ID, T>.folding(): Fold<Giorgio, Destination> = TODO()
//
//fun <InputType : Comparable<InputType>, ReturnType> FieldsSummarize<InputType, InputType, ReturnType>.max() = op { acc: InputType, entry: InputType ->
//
//}

fun main() {
    val field: Field<String, Int> = TODO() // Initialize your field here
    val maxValue = field.summarize().includingSelf.max()
    println("Max value: $maxValue")
}
