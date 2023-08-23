const publishCmd = `
./gradlew -PstagingRepositoryId=\${process.env.STAGING_REPO_ID} releaseStagingRepositoryOnMavenCentral || exit 1
`;

const config = require('semantic-release-preconfigured-conventional-commits');
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
module.exports = config
