plugins {
    androidApplication
    androidLibrary
    kaptPlugin
    daggerHilt
    navigationSafeArgsKotlin
    kotlinParcelize
}

kotlin {
    jvmToolchain(17)
}

android {
    compileSdk = Versions.compilesdk
    namespace = "ng.mint.ocrscanner"
    defaultConfig {
        applicationId = Application.id
        minSdk = Versions.minsdk
        targetSdk = Versions.targetsdk
        versionCode = Application.versionCode
        versionName = Application.versionName
        testInstrumentationRunner = Application.testInstrumentationRunner
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
        exclude("META-INF/*.kotlin_module")
    }

    testOptions {
        animationsDisabled = true
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("io.card:android-sdk:5.5.1")
    implementAll(Dependencies.implementations)
    implementAll(SupportDependencies.supportImplementation)
    testImplementAll(TestDependencies.testImplementation)
    testImplementation("org.hamcrest:hamcrest:2.2")
    testAndroidImplementAll(AndroidTestDependencies.androidTestImplementation)
    kaptImplementAll(AnnotationProcessors.AnnotationProcessorsImplementation)
    kaptAndroidTestImplementAll(AnnotationProcessors.AnnotationProcessorsImplementation)
    debugImplementationAll(DebugDependencies.debugImplementation)
}