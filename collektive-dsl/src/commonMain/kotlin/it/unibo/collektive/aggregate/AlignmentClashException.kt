/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate

import it.unibo.collektive.networking.OutboundEnvelope.SharedData
import it.unibo.collektive.networking.PayloadRepresentation
import it.unibo.collektive.path.Path

/**
 * Exception thrown when an alignment clash occurs on a [path], storing both [previous] and [current].
 */
class AlignmentClashException(val path: Path, val previous: PayloadRepresentation<*>?, val current: SharedData<*, *>) :
    IllegalStateException() {

    override val message: String = """
        |Aggregate alignment clash originated at the same path:
        |${path.toMultilineString(separator = "\n|")}
        |
        |The default payload value that had been aligned before (excluding overrides) was: $previous
        |The value that is being aligned now is: $current
        |
        |(even if they are the same, Collektive does not tolerate clashes, as they are symptomatic of a bug in the code)
        |
        |Possible causes are:
        |    - the collektive compiler plugin is not enabled, or
        |    - multiple aligned function calls with the same alignment, most likely inside a loop without proper manual alignment.
        |If none of the above apply, please file an issue at https://github.com/Collektive/collektive/issues
    """.trimMargin()
}
