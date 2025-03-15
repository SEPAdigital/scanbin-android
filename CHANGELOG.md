# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

