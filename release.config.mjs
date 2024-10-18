const publishCmd = `
git -C site/build add . || exit 1
git -C site/build commit -m "chore: update website to version \${nextRelease.version}" || exit 2
git -C site/build push || exit 3
./gradlew gradle-plugin:publishPlugins -Pgradle.publish.key=$GRADLE_PUBLISH_KEY -Pgradle.publish.secret=$GRADLE_PUBLISH_SECRET || exit 4
./gradlew -PstagingRepositoryId=\${process.env.STAGING_REPO_ID} releaseStagingRepositoryOnMavenCentral || exit 5
`;

import config from 'semantic-release-preconfigured-conventional-commits' assert { type: "json" }

config.plugins.push(
    [
        "@semantic-release/exec",
        {
            "publishCmd": publishCmd,
        }
    ],
    "@semantic-release/github",
    "@semantic-release/git",
)

export default config;
