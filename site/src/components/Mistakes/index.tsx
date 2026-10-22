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
import styles from './styles.module.css';

/** A card grouping one mistake: its heading, symptom, code, and explanation. */
export function Mistake({children}: {children: ReactNode}): ReactNode {
    return <section className={styles.mistake}>{children}</section>;
}

/** How the mistake shows itself. */
export function Symptom({children}: {children: ReactNode}): ReactNode {
    return (
        <div className={styles.symptom}>
            <span className={styles.symptomLabel}>
                <svg viewBox="0 0 16 16" aria-hidden="true">
                    <path d="M8 1.8 15 14H1z" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinejoin="round" />
                    <path d="M8 6.2v3.6M8 11.6v.4" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" />
                </svg>
                Symptom
            </span>
            <div className={styles.symptomText}>{children}</div>
        </div>
    );
}

type Verdict = 'dont' | 'do';

type PanelProps = {
    /** A short note next to the title. */
    label?: string;
    /** What the code produces, shown below it. */
    result?: ReactNode;
    children: ReactNode;
};

function Panel({verdict, label, result, children}: PanelProps & {verdict: Verdict}): ReactNode {
    const title = verdict === 'do' ? 'Do' : "Don't";
    return (
        <div className={clsx(styles.panel, verdict === 'do' ? styles.panelDo : styles.panelDont)}>
            <div className={styles.panelHead}>
                <span className={styles.panelIcon} aria-hidden="true">{verdict === 'do' ? '✓' : '✕'}</span>
                <span className={styles.panelTitle}>{title}</span>
                {label && <span className={styles.panelLabel}>{label}</span>}
            </div>
            <div className={styles.panelBody}>{children}</div>
            {result && <div className={styles.panelResult}>{result}</div>}
        </div>
    );
}

/** Code to avoid. */
export function Dont(props: PanelProps): ReactNode {
    return <Panel verdict="dont" {...props} />;
}

/** The code to write instead. */
export function Do(props: PanelProps): ReactNode {
    return <Panel verdict="do" {...props} />;
}

/** A wrong and a right version of the same code, one after the other. */
export function DoDont({children}: {children: ReactNode}): ReactNode {
    return <div className={styles.doDont}>{children}</div>;
}

/** Why it happens, or how to fix it. */
export function Explanation({title = 'Why', children}: {title?: string; children: ReactNode}): ReactNode {
    return (
        <div className={styles.explanation}>
            <span className={styles.explanationLabel}>{title}</span>
            <div className={styles.explanationText}>{children}</div>
        </div>
    );
}

/** A diagnostic of the compiler plugin: its severity, identifier, and message. */
export function Diagnostic({id, severity, children}: {id: string; severity: 'warning' | 'error'; children: ReactNode}): ReactNode {
    return (
        <div className={styles.diagnostic}>
            <div className={styles.diagnosticHead}>
                <span className={clsx(styles.severity, severity === 'error' ? styles.severityError : styles.severityWarning)}>{severity}</span>
                <code className={styles.diagnosticId}>{id}</code>
            </div>
            <div className={styles.diagnosticMessage}>{children}</div>
        </div>
    );
}

type Entry = {to: string; title: string};

/** An index of the mistakes, grouped by topic. */
export function MistakeIndex({groups}: {groups: {name: string; entries: Entry[]}[]}): ReactNode {
    return (
        <nav className={styles.index} aria-label="Mistakes on this page">
            {groups.map((group) => (
                <div key={group.name} className={styles.indexGroup}>
                    <span className={styles.indexName}>{group.name}</span>
                    <ul>
                        {group.entries.map((entry) => (
                            <li key={entry.to}><Link to={entry.to}>{entry.title}</Link></li>
                        ))}
                    </ul>
                </div>
            ))}
        </nav>
    );
}
