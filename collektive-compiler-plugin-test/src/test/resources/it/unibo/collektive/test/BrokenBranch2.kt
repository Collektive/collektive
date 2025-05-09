/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */
package it.unibo.collektive.test

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.stdlib.ints.FieldedInts.plus

fun exchangeWithThreeDevices(body: Aggregate<Int>.(Field<Int, Int>) -> Field<Int, Int>): Unit = TODO()

fun asd() = exchangeWithThreeDevices {
    if (localId % 2 == 0) {
        neighboring(1) + it
    } else {
        neighboring(1) + it
    }
}
