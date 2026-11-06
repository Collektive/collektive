/*
 * Copyright (c) 2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

import React, {type ReactNode} from 'react';
import Link from '@docusaurus/Link';
import styles from './styles.module.css';

export type Layer = {
    name: string;
    /** What the layer offers, as short labels. */
    items: string[];
    /** Where it lives in Collektive. */
    collektive?: {label: string; to?: string};
};

/** A stack of abstraction layers, drawn top (most abstract) to bottom. */
export default function LayerStack({layers, caption}: {layers: Layer[]; caption?: ReactNode}): ReactNode {
    return (
        <figure className={styles.figure}>
            <div className={styles.stack}>
                {layers.map((layer, index) => (
                    <div key={layer.name} className={styles.layer} style={{'--depth': index} as React.CSSProperties}>
                        <div className={styles.name}>{layer.name}</div>
                        <div className={styles.items}>
                            {layer.items.map((item) => (
                                <span key={item} className={styles.item}>{item}</span>
                            ))}
                        </div>
                        {layer.collektive && (
                            <div className={styles.where}>
                                {layer.collektive.to ? (
                                    <Link to={layer.collektive.to}>{layer.collektive.label}</Link>
                                ) : (
                                    layer.collektive.label
                                )}
                            </div>
                        )}
                    </div>
                ))}
            </div>
            {caption && <figcaption className={styles.caption}>{caption}</figcaption>}
        </figure>
    );
}
