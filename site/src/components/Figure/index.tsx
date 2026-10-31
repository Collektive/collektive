/*
 * Copyright (c) 2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

import React, {type ReactNode} from 'react';
import clsx from 'clsx';
import useBaseUrl from '@docusaurus/useBaseUrl';
import styles from './styles.module.css';

type FigureProps = {
    src: string;
    alt: string;
    /** Maximum width of the image (e.g., 420 or '60%'). */
    width?: number | string;
    /** Draws the image on a light card: use it for figures that are not readable on a dark background. */
    card?: boolean;
    /** Optional short label shown before the caption, e.g. "(a)". */
    label?: string;
    /** The caption (markdown is supported). */
    children?: ReactNode;
};

/** A centered figure with an optional caption. */
export default function Figure({src, alt, width, card, label, children}: FigureProps): ReactNode {
    return (
        <figure className={styles.figure}>
            <div className={clsx(styles.frame, card && styles.card)} style={{maxWidth: width ?? '100%'}}>
                <img src={useBaseUrl(src)} alt={alt} />
            </div>
            {(children || label) && (
                <figcaption className={styles.caption}>
                    {label && <span className={styles.label}>{label}</span>}
                    {children}
                </figcaption>
            )}
        </figure>
    );
}

type FigureGridProps = {
    /** Number of columns on wide screens (one column on phones). */
    columns?: number;
    children: ReactNode;
};

/** Lays out figures side by side, with aligned images and captions. */
export function FigureGrid({columns, children}: FigureGridProps): ReactNode {
    const count = columns ?? React.Children.count(children);
    return (
        <div className={styles.grid} style={{'--figure-columns': count} as React.CSSProperties}>
            {children}
        </div>
    );
}
