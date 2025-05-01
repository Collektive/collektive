/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

import React from 'react';
import clsx from 'clsx';
import styles from './HeroSection.module.css';
import Link from '@docusaurus/Link';
import Heading from "@theme/Heading";

const CollektiveLogo = require('@site/static/img/collektive-logo-white-background-round.svg').default;

export default function HeroSection() {
    return (
        <section className={clsx('hero shadow--lw', styles.heroModern)}>
            <div className={clsx('col col--6', styles.heroContent)}>
                <Heading as="h1" className="hero__title">
                    Collektive
                </Heading>
                <Heading as="h3" className="hero__subtitle">Practical. Scalable. Kotlin-first.</Heading>
                <Link
                    className="button button--secondary button--lg"
                    to="/docs/intro">
                    Get Started
                </Link>
            </div>
            <div className={clsx('col col--6', styles.heroImage)}>
                <CollektiveLogo className={styles.heroImage} alt="Collektive Logo" />
            </div>
        </section>
    );
}
