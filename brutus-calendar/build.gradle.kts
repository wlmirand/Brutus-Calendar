import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
}

kotlin {
    explicitApi()
    jvmToolchain(21)

    android {
        namespace = "william.miranda.brutuscalendar"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
        withHostTestBuilder {}.configure {}
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.ui)
            api(libs.kotlinx.datetime)
            api(libs.compose.foundation)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    //coordinates(project.group.toString(), "brutus-calendar", project.version.toString())
    publishToMavenCentral()
    // Local builds and Maven Local need no credentials. CI enables signing explicitly.
    if (providers.gradleProperty("releaseSigning").orNull == "true") {
        signAllPublications()
    }

    pom {
        name.set("BrutusCalendar")
        description.set("A customizable calendar library for Compose Multiplatform.")
        inceptionYear.set("2026")
        url.set("https://github.com/wlmirand/BrutusCalendar")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("wlmirand")
                name.set("William Miranda")
                url.set("https://github.com/wlmirand")
            }
        }
        scm {
            url.set("https://github.com/wlmirand/BrutusCalendar")
            connection.set("scm:git:https://github.com/wlmirand/BrutusCalendar.git")
            developerConnection.set("scm:git:ssh://git@github.com/wlmirand/BrutusCalendar.git")
        }
    }
}
