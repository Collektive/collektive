/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api

/**
 * Indicates that a function, class, or file should not be automatically instrumented
 * with alignment behavior by the Collektive compiler plugin.
 *
 * This annotation can be used to opt out of automatic alignment injection for specific
 * aggregate DSL constructs when such instrumentation is not desired or would interfere
 * with custom logic.
 *
 * @property explanation An optional string providing the rationale for skipping alignment,
 * useful for tooling and documentation.
 *
 * ### Applicable Targets
 * - **Function**: Prevents alignment on a per-function basis.
 * - **Class**: Disables alignment injection for all contained functions.
 * - **File**: Globally disables alignment injection for the entire file.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class PurelyLocal(val explanation: String = "")
