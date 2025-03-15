# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.0] - 2025-03-15

### Changed
- Upgraded Java version from 11 to 17 (Latest LTS)
- Updated Gradle from 8.2.0 to 8.3.0
- Updated Dagger/Hilt dependencies from 2.50 to 2.51
- Updated mockitoDexMaker from 2.2.0 to 2.28.0
- Updated foojay-resolver plugin from 0.5.0 to 0.7.0
- Configured proper Java 17 toolchain support in build scripts
- Updated build configuration for better dependency management
- Maintained all other dependencies at their latest stable versions

### Security
- Updated dependencies to their latest stable versions to address potential security vulnerabilities

### Compatibility
- Maintained minimum SDK version at 24
- Updated target and compile SDK to 34

## [1.0.1] - 2025-03-15

### Added
- POST_NOTIFICATIONS permission for Android 13+ compatibility
- Camera hardware feature declaration with `required="false"` for better device compatibility

### Changed
- Updated WorkManager initialization to use on-demand initialization
- Improved fragment lifecycle handling in CardInformationFragment
- Removed default WorkManagerInitializer from AndroidManifest.xml

### Fixed
- WorkManager initialization conflicts
- Notification permission handling for Android 13+
- Unsafe lifecycle usage in fragments
- Chrome OS compatibility with camera hardware

