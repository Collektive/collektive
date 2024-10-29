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
./gradlew gradle-plugin:publishPlugins -Pgradle.publish.key=$GRADLE_PUBLISH_KEY -Pgradle.publish.secret=$GRADLE_PUBLISH_SECRET || exit 5
./gradlew -PstagingRepositoryId=\${process.env.STAGING_REPO_ID} releaseStagingRepositoryOnMavenCentral || exit 6
`;

import config from 'semantic-release-preconfigured-conventional-commits' assert {type: 'json'}

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
