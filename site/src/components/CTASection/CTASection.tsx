/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

import React from 'react';
import clsx from 'clsx';
import styles from './CTASection.module.css';
import Link from '@docusaurus/Link';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faGithub} from "@fortawesome/free-brands-svg-icons";

export default function CTASection() {
    return (
        <section className={clsx('container', styles.ctaSection)}>
            <div className="row">
                <div className="col col--8 col--offset-2 text--center">
                    <h2>Join the Collektive Community</h2>
                    <p>Contribute, ask questions, and help shape the next-gen Aggregate framework.</p>
                    <Link className="button button--secondary button--lg" href="https://github.com/Collektive">
                        <FontAwesomeIcon icon={faGithub} size="lg" /> Join on GitHub
                    </Link>
                </div>
            </div>
        </section>
    );
}
