/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

import React from 'react';
import clsx from 'clsx';
import styles from './CodeShowcase.module.css';
import CodeBlock from "@theme/CodeBlock";
import Heading from "@theme/Heading";

const codeNeighbor = `/**
 * Each node computes the distance from the source node.
 */
fun Aggregate<Int>.gradient(collektiveDevice: CollektiveDevice<*>, source: Boolean): Double =
    share(POSITIVE_INFINITY) {
        val dist = with(collektiveDevice) { distances() }
        val minValue = (it + dist).min(POSITIVE_INFINITY)
        when {
            source -> 0.0
            else -> minValue
        }
    }
`

export default function CodeShowcase() {
    return (
        <section className={clsx(styles.codeSection)}>
            <div className="container">
                <Heading as="h2" className="text--center">Practical Aggregate Language</Heading>
                <p className="text--center">
                    Define your collective system using aggregate operators.
                </p>
                <CodeBlock language="kotlin" className={clsx('padding--lg', styles.codeBlock)}>{codeNeighbor}</CodeBlock>
            </div>
        </section>
    );
}
