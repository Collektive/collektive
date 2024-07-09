const publishCmd = `
./gradlew gradle-plugin:publishPlugins -Pgradle.publish.key=$GRADLE_PUBLISH_KEY -Pgradle.publish.secret=$GRADLE_PUBLISH_SECRET || exit 1
./gradlew -PstagingRepositoryId=\${process.env.STAGING_REPO_ID} releaseStagingRepositoryOnMavenCentral || exit 2
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
