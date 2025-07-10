# GitHub Actions Workflows

This directory contains GitHub Actions workflows for automating various tasks in the factoryfx repository.

## Publishing to Maven Central

The `publish.yml` workflow automates the process of publishing artifacts to Maven Central using the GradleUp/nmcp plugin. It can be triggered either:
- Automatically when a new release is created
- Manually via workflow dispatch (with optional version override)

The project uses the [GradleUp/nmcp](https://github.com/GradleUp/nmcp) plugin to simplify the Maven Central publishing process.

### Required Secrets

To use the publishing workflow, you need to set up the following secrets in your GitHub repository:

1. **OSSRH_USERNAME**: Your Sonatype OSSRH username
2. **OSSRH_PASSWORD**: Your Sonatype OSSRH password
3. **GPG_SIGNING_KEY**: Your GPG private key in ASCII-armored format
4. **GPG_SIGNING_PASSWORD**: The passphrase for your GPG key

### Setting up GPG for GitHub Actions

To prepare your GPG key for GitHub Actions:

1. Export your GPG private key in ASCII-armored format:
   ```
   gpg --export-secret-keys --armor YOUR_KEY_ID > private.key
   ```

2. Copy the entire content of the `private.key` file (including the BEGIN/END markers) and add it as the `GPG_SIGNING_KEY` secret in GitHub.

3. Add your GPG passphrase as the `GPG_SIGNING_PASSWORD` secret in GitHub.

### Manual Publishing

To manually publish a new version:

1. Go to the "Actions" tab in your GitHub repository
2. Select the "Publish to Maven Central" workflow
3. Click "Run workflow"
4. Optionally enter a version number to override the one in build.gradle
5. Click "Run workflow" to start the publishing process

The workflow will build the project, sign the artifacts, and publish them to Maven Central using the GradleUp/nmcp plugin's `publishToMavenCentral` task.
