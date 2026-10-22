/*
 * Copyright (c) 2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

import React, {type ReactNode} from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import type {IconDefinition} from '@fortawesome/fontawesome-svg-core';
import {
    faBook,
    faBookOpen,
    faBug,
    faCode,
    faCodeBranch,
    faCompass,
    faCube,
    faDiagramProject,
    faFlask,
    faGraduationCap,
    faLayerGroup,
    faMicrochip,
    faPlay,
    faPuzzlePiece,
    faRocket,
    faShieldHalved,
    faSitemap,
    faTriangleExclamation,
    faWandMagicSparkles,
    faArrowsLeftRight,
} from '@fortawesome/free-solid-svg-icons';
import {faAndroid, faGithub} from '@fortawesome/free-brands-svg-icons';
import styles from './styles.module.css';

const ICONS: Record<string, IconDefinition> = {
    android: faAndroid,
    book: faBook,
    'book-open': faBookOpen,
    bug: faBug,
    code: faCode,
    branch: faCodeBranch,
    compass: faCompass,
    cube: faCube,
    diagram: faDiagramProject,
    flask: faFlask,
    github: faGithub,
    learn: faGraduationCap,
    layers: faLayerGroup,
    chip: faMicrochip,
    multiplatform: faArrowsLeftRight,
    play: faPlay,
    puzzle: faPuzzlePiece,
    rocket: faRocket,
    shield: faShieldHalved,
    sitemap: faSitemap,
    warning: faTriangleExclamation,
    magic: faWandMagicSparkles,
};

export type CardProps = {
    title: ReactNode;
    /** Where the card leads; a card without a link is just a box. */
    to?: string;
    /** One of the icon names of this module, e.g. "rocket". */
    icon?: string;
    /** A small label above the title, e.g. "Tutorial". */
    eyebrow?: string;
    children?: ReactNode;
};

/** A card with an icon, a title, and a short description. */
export function Card({title, to, icon, eyebrow, children}: CardProps): ReactNode {
    const body = (
        <>
            {icon && ICONS[icon] && (
                <span className={styles.icon} aria-hidden="true">
                    <FontAwesomeIcon icon={ICONS[icon]} />
                </span>
            )}
            <div className={styles.text}>
                {eyebrow && <span className={styles.eyebrow}>{eyebrow}</span>}
                <span className={styles.title}>
                    {title}
                    {to && <span className={styles.arrow} aria-hidden="true">→</span>}
                </span>
                {children && <div className={styles.description}>{children}</div>}
            </div>
        </>
    );
    return to ? (
        <Link to={to} className={clsx(styles.card, styles.link)}>{body}</Link>
    ) : (
        <div className={styles.card}>{body}</div>
    );
}

/** A responsive grid of cards. */
export function CardGrid({columns = 2, children}: {columns?: 1 | 2 | 3; children: ReactNode}): ReactNode {
    return <div className={clsx(styles.grid, styles[`columns${columns}`])}>{children}</div>;
}
