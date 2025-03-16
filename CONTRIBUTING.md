# Contributing to ScanBIN Android

Thank you for your interest in contributing to ScanBIN Android! This document provides guidelines and steps for contributing.

## Development Process

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Commit Messages

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

- feat: A new feature
- fix: A bug fix
- docs: Documentation changes
- style: Code style changes (formatting, missing semi-colons, etc)
- refactor: Code refactoring
- test: Adding or updating tests
- chore: Maintenance tasks

## Code Style

- Follow Android/Kotlin coding conventions
- Write meaningful comments
- Include unit tests for new features
- Update documentation when needed

## Running Tests

Before submitting a PR, ensure all tests pass:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Code of Conduct

This project follows the [Contributor Covenant](https://www.contributor-covenant.org/version/2/0/code_of_conduct/) Code of Conduct.

## License

By contributing to ScanBIN Android, you agree that your contributions will be licensed under the MIT License.

