/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

import React from 'react';
import clsx from 'clsx';
import styles from './FeatureStrip.module.css';
import "../../css/custom.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faArrowsLeftRight, faGears, faRocket} from "@fortawesome/free-solid-svg-icons";

const features = [
    {
        title: 'Powerful DSL',
        icon: <FontAwesomeIcon icon={faRocket} size="8x" color="var(--ifm-font-color-base)" />,
        description: 'Design collective systems using concise, type-safe Kotlin syntax.',
    },
    {
        title: 'Fully Multiplatform',
        icon: <FontAwesomeIcon icon={faArrowsLeftRight} size="8x" color="var(--ifm-font-color-base)" />,
        description: 'Run on JVM, JS, Native, Android, and iOS. Write once, run anywhere. For real.',
    },
    {
        title: 'Composable Architecture',
        icon: <FontAwesomeIcon icon={faGears} size="8x" color="var(--ifm-font-color-base)" />,
        description: 'Compose your system from reusable, collective functions.',
    },
];

export default function FeatureStrip() {
    return (
        <section className={clsx('container', styles.featureStrip)}>
            <div className="row">
                {features.map((f, idx) => (
                    <div key={idx} className="col col--4 text--center">
                        <div className={styles.featureIcon}>
                            {f.icon}
                        </div>
                        <p/>
                        <h3>{f.title}</h3>
                        <p>{f.description}</p>
                    </div>
                ))}
            </div>
        </section>
    );
}
