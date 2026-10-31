/*
 * Copyright (c) 2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

import React, {type ReactElement, type ReactNode} from 'react';
import styles from './styles.module.css';

const slug = (name: string): string =>
    name.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');

type TermProps = {
    /** The term being defined. */
    name: string;
    /** Alternative names, shown below the term. */
    aka?: string;
    /** The definition (markdown is supported). */
    children: ReactNode;
};

/** A single glossary entry, linkable as #term-name. */
export function Term({name, aka, children}: TermProps): ReactNode {
    const id = slug(name);
    return (
        <div className={styles.term} id={id}>
            <a className={styles.name} href={`#${id}`}>{name}</a>
            {aka && <div className={styles.aka}>{aka}</div>}
            <div className={styles.definition}>{children}</div>
        </div>
    );
}

/** Groups the [Term]s by initial letter, with a letter index on top. */
export default function Glossary({children}: {children: ReactNode}): ReactNode {
    const terms = React.Children.toArray(children)
        .filter((child): child is ReactElement<TermProps> => React.isValidElement(child))
        .sort((a, b) => a.props.name.localeCompare(b.props.name));
    const groups = new Map<string, ReactElement<TermProps>[]>();
    terms.forEach((term) => {
        const letter = term.props.name.replace(/[^A-Za-z]/g, '').charAt(0).toUpperCase();
        groups.set(letter, [...(groups.get(letter) ?? []), term]);
    });
    return (
        <div>
            <nav className={styles.index} aria-label="Glossary index">
                {[...groups.keys()].map((letter) => (
                    <a key={letter} href={`#letter-${letter}`}>{letter}</a>
                ))}
            </nav>
            {[...groups.entries()].map(([letter, entries]) => (
                <section key={letter} className={styles.group}>
                    <h2 id={`letter-${letter}`} className={styles.letter}>{letter}</h2>
                    <div className={styles.grid}>{entries}</div>
                </section>
            ))}
        </div>
    );
}
