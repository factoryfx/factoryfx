# GitHub Actions Workflows

This directory contains GitHub Actions workflows for automating various tasks in the factoryfx repository.

## Publishing to Maven Central

The `publish.yml` workflow automates the process of publishing artifacts to Maven Central using the GradleUp/nmcp plugin. It can be triggered either:
- Automatically when a new release is created
- Manually via workflow dispatch (with optional version override)

The project uses the [GradleUp/nmcp](https://github.com/GradleUp/nmcp) plugin to simplify the Maven Central publishing process.

### Required Secrets

To use the publishing workflow, you need to set up the following secrets in your GitHub repository:

1. **SONATYPE_USER**: Your Sonatype username
2. **SONATYPE_PASSWORD**: Your Sonatype password
3. **SECRING**: Your GPG private key in ASCII-armored format
4. **SECRING_PASS**: The passphrase for your GPG key


### Manual Publishing

To manually publish a new version:

1. Change the version in build.gradle
2. Create a [new Release](https://github.com/factoryfx/factoryfx/releases/new)
3. Create a new Tag with the version
5. Click "Publish release" to start the publishing process

The workflow will build the project, sign the artifacts, and publish them to Maven Central using the GradleUp/nmcp plugin's `publishAggregationToCentralPortal` task.
