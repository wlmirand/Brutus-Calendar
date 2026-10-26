# BrutusCalendar

A Compose Multiplatform library scaffold by William Miranda, licensed under Apache-2.0.

**The calendar is not implemented in this repository yet.** The demo only contains
a placeholder screen. This project is ready to receive the existing calendar code
without defining the library API in advance.

## Project structure

| Directory | Purpose |
| --- | --- |
| `brutus-calendar/` | Publishable KMP library with no Material dependency |
| `demo/` | Shared demo UI using Material 3 |
| `androidApp/` | Android application |
| `iosApp/` | SwiftUI/Xcode application integrated directly with the demo framework |
| `.github/workflows/` | Validation and manually triggered uploads to Maven Central |

The root Gradle project is only an aggregator. Only `brutus-calendar` is published.

## Platforms and tools

- Android **8.0+ (API 26)**; compile/target SDK **37**.
- iOS **15+**: ARM64 devices and Apple Silicon simulators (`iosArm64`, `iosSimulatorArm64`).
- Desktop, Web, and Intel iOS simulator targets are outside the initial scope.
- JDK **21** for Gradle and the toolchain; Android JVM **17** bytecode.
- For iOS: macOS, **Xcode 26.4**, and an installed simulator runtime.
- Android Studio compatible with AGP 9.3.1 and Android SDK Platform 37 / Build Tools 36.0.0.

Versions checked on **October 26, 2026**:

| Component | Version |
| --- | --- |
| Kotlin and Compose Compiler | 2.4.21 |
| Compose Multiplatform | 1.12.2 |
| Android Gradle Plugin | 9.3.1 |
| Gradle Wrapper | 9.7.0 |
| Material 3 Multiplatform (demo) | 1.9.0 |
| Activity Compose (Android demo) | 1.13.0 |
| Vanniktech Maven Publish | 0.37.0 |

Plugin and library versions are defined in `gradle/libs.versions.toml`.
The Wrapper is configured in `gradle/wrapper/gradle-wrapper.properties`, including the distribution checksum.
Material 3 is versioned independently of Compose Multiplatform.

Gradle 9.8.2 and AGP 9.4.1 were already available when checked, but were not selected:
the [official Kotlin 2.4.21 compatibility matrix](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-compatibility-guide.html)
declares support up to Gradle 9.7.0 and AGP 9.3.1. No prerelease versions were selected.
See also [Compose Multiplatform compatibility](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compatibility-and-versioning.html).

## Running the demo

The commands below are instructions for local use; no builds were run when this
scaffold was created. The first Gradle sync or execution downloads the distribution
and dependencies. iOS builds may also download Kotlin/Native tools.

### Android

1. Open the project root in Android Studio with JDK 21 configured.
2. Configure the SDK through Android Studio or in `local.properties` (not tracked):
   `sdk.dir=/path/to/Android/sdk`.
3. Run the `androidApp` module configuration on an API 26+ emulator or device.

Alternatively, with the environment already configured:

```sh
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug
```

### iOS

1. On your Mac, set `JAVA_HOME` to JDK 21 and select Xcode 26.4.
2. Set up the Android SDK as well: the same Gradle project includes the Android modules.
3. Open `iosApp/iosApp.xcodeproj` and select the `iosApp` scheme.
4. Select an Apple Silicon simulator and run the app. For a physical device,
   configure your team under **Signing & Capabilities**.

The **Build Kotlin framework** phase calls `:demo:embedAndSignAppleFrameworkForXcode`
before compiling Swift. Neither CocoaPods nor another project generation tool is required.
The deployment target is set in `iosApp/iosApp.xcodeproj/project.pbxproj` (15.0),
which is also the default minimum for Kotlin/Native 2.4.21.

## Adding the calendar

Place the shared code in:

```text
brutus-calendar/src/commonMain/kotlin/william/miranda/brutuscalendar/
```

`Calendar.kt` only contains a comment and can be replaced with your code.
The library already depends on Compose Runtime, UI, and Foundation. `explicitApi()`
requires explicit visibility and types in the public API. Add tests under
`brutus-calendar/src/commonTest/kotlin/` when adding the implementation.

The demo already depends on the library module. Replace the placeholder screen in
`demo/src/commonMain/kotlin/william/miranda/brutuscalendar/demo/App.kt`
with examples using the actual API.

## Publishing

Configured coordinates, **not published yet**:

```kotlin
implementation("io.github.wlmirand:brutus-calendar:0.1.0")
```

The Kotlin package remains `william.miranda.brutuscalendar`, independently of the
Maven Group ID. `GROUP` and `VERSION_NAME` are defined in `gradle.properties`.

Before the first publication:

1. Create the `wlmirand/BrutusCalendar` repository on GitHub and push the code yourself.
2. Add the implementation and its tests; do not publish just the scaffold.
3. Register with the [Sonatype Central Portal](https://central.sonatype.com/) and
   verify the **`io.github.wlmirand`** namespace. The namespace has not been verified yet.
4. Generate a **Central Portal user token** (not your login password).
5. Prepare a GPG signing key and distribute its **public** key to a keyserver
   accepted by Central. Follow the [official requirements](https://central.sonatype.org/publish/requirements/gpg/).
6. Create the **`maven-central`** GitHub environment, preferably with required
   approval and restricted to the default branch, and configure these secrets:

   | Secret | Contents |
   | --- | --- |
   | `MAVEN_CENTRAL_USERNAME` | Username from the Central Portal user token |
   | `MAVEN_CENTRAL_PASSWORD` | Password from the same user token |
   | `SIGNING_IN_MEMORY_KEY` | Complete multiline ASCII-armored GPG private key |
   | `SIGNING_IN_MEMORY_KEY_PASSWORD` | GPG key passphrase, if any |

7. Wait for CI validation. Under **Actions → Publish to Maven Central → Run workflow**,
   select the default branch and enter the version already defined in `gradle.properties`.
8. The workflow uploads signed Android and iOS publications and KMP metadata, but
   **does not release automatically**. Review the deployment in the Central Portal
   and click **Publish**. Published versions cannot be overwritten.

Never store tokens, passwords, or private keys in the repository or send them to the agent.
The workflow does not create tags, commits, GitHub releases, or pushes.

To publish only to Maven Local without credentials or signing (use macOS to
generate all iOS targets):

```sh
./gradlew :brutus-calendar:publishToMavenLocal
```

The plugin generates a sources JAR and an empty javadoc JAR by default. Once the
API exists, adding KDoc/Dokka documentation before a public release is recommended.
For details, see the [publishing plugin documentation](https://vanniktech.github.io/gradle-maven-publish-plugin/central/).

## CI and validation status

- `validate.yml`: builds the Android demo, runs `check` on the library, and, on
  macOS, builds the iOS app without signing and runs simulator tests when available.
- `publish.yml`: manually triggered on the default branch only, with secrets
  exposed only to the Central Portal upload step.
- Actions are pinned by SHA. The Gradle Wrapper was obtained from the official
  upstream, and its JAR SHA-256 was checked against the checksum published by Gradle.
- The workflows assume a `macos-26` runner with Xcode 26.4 available; adjust the
  runner if necessary based on the images available in GitHub Actions.
- **Builds, tests, and workflows have not been run yet.** Compatibility was selected
  based on documentation, not confirmed by compilation.

## License

Copyright 2026 William Miranda. Licensed under [Apache-2.0](LICENSE).
