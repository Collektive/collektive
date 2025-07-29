/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

const projects = ["", ":collektive-compiler-plugin:", ":collektive-gradle-plugin:", ":collektivize:"];
const tasks = ["publishAllPublicationsToProjectLocalRepository", "zipMavenCentralPortalPublication", "releaseMavenCentralPortalPublication"];
const gradleTasks = projects.flatMap(p => tasks.map(t => `${p}${t}`)).join(' ');
const publishCmd = `
if [ -n "\$(git -C site/build status --porcelain)" ]; then
    echo "site directory changes detected, new site release..."
    git -C site/build add . || exit 1
    git -C site/build commit -m "chore: update website to version \${nextRelease.version}" || exit 2
else
    echo "no site directory changes detected, pushing empty commit..."
    git -C site/build commit --allow-empty -m "chore: new release \${nextRelease.version}, no website updates" || exit 3
fi
git -C site/build push || exit 4
./gradlew -PforceVersion=\${nextRelease.version} collektive-gradle-plugin:publishPlugins -Pgradle.publish.key=$GRADLE_PUBLISH_KEY -Pgradle.publish.secret=$GRADLE_PUBLISH_SECRET || exit 5
./gradlew -PforceVersion=\${nextRelease.version} collektivize:publishPlugins -Pgradle.publish.key=$GRADLE_PUBLISH_KEY -Pgradle.publish.secret=$GRADLE_PUBLISH_SECRET || exit 6
./gradlew ${gradleTasks} || exit 7
`;

import config from 'semantic-release-preconfigured-conventional-commits' with {type: 'json'};

config.plugins.push(
    [
        "@semantic-release/exec",
        {
            "publishCmd": publishCmd,
        }
    ],
    "@semantic-release/github",
    "@semantic-release/git",
);

export default config;
