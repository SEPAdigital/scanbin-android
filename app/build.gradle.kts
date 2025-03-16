plugins {
    androidApplication
    androidLibrary
    kaptPlugin
    daggerHilt
    navigationSafeArgsKotlin
    kotlinParcelize
    id("com.google.devtools.ksp")
    id("jacoco")  // JaCoCo plugin for coverage reporting
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
        
        // Enable test orchestrator for clean test state
        testInstrumentationRunnerArguments += mapOf(
            "clearPackageData" to "true"
        )
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        
        getByName("debug") {
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
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all"
        )
    }

    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/LICENSE")
            excludes.add("META-INF/LICENSE.txt")
            excludes.add("META-INF/license.txt")
            excludes.add("META-INF/NOTICE")
            excludes.add("META-INF/NOTICE.txt")
            excludes.add("META-INF/notice.txt")
            excludes.add("META-INF/AL2.0")
            excludes.add("META-INF/LGPL2.1")
            excludes.add("META-INF/*.kotlin_module")
            excludes.add("META-INF/versions/9/previous-compilation-data.bin")
        }
        jniLibs {
            useLegacyPackaging = false
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            // Enable JUnit test coverage reporting
            all {
                it.extensions.configure(JacocoTaskExtension::class) {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                }
            }
        }
        animationsDisabled = true
        
        // Configure test orchestrator
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        
        // Device matrix for testing different screen sizes and orientations
        managedDevices {
            devices {
                // Phone device - Portrait
                maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("phonePortrait").apply {
                    device = "Pixel 6"
                    apiLevel = 33
                    systemImageSource = "google"
                }
                
                // Tablet device
                maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("tabletDevice").apply {
                    device = "Pixel Tablet"
                    apiLevel = 33
                    systemImageSource = "google"
                }
                
                // Legacy device
                maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("legacyDevice").apply {
                    device = "Pixel 4"
                    apiLevel = 28
                    systemImageSource = "google"
                }
            }
            groups {
                maybeCreate("screenSizes").apply {
                    targetDevices.add(devices["phonePortrait"])
                    targetDevices.add(devices["tabletDevice"])
                }
                
                maybeCreate("apiMatrix").apply {
                    targetDevices.add(devices["phonePortrait"])
                    targetDevices.add(devices["legacyDevice"])
                }
            }
        }
    }
    
    lint {
        abortOnError = false
        checkReleaseBuilds = true
        checkDependencies = true
        disable += listOf(
            "InvalidPackage",
            "ObsoleteSdkInt",
            "GradleDependency"
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all"
        )
    }
}

// JaCoCo test coverage configuration
jacoco {
    toolVersion = "0.8.10"
}

// Create JaCoCo test report task
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*Hilt*.*",
        "**/*_Factory*.*",
        "**/*Module*.*",
        "**/*Dagger*.*",
        "**/*MembersInjector*.*",
        "**/*_Provide*.*"
    )
    
    val mainSrc = "${project.projectDir}/src/main/java"
    val debugTree = "${buildDir}/tmp/kotlin-classes/debug"
    
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(fileTree(debugTree) {
        exclude(fileFilter)
    }))
    executionData.setFrom(fileTree(buildDir) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
}

// Configure KAPT settings - not needed for Moshi anymore since we use KSP
kapt {
    correctErrorTypes = true
    useBuildCache = true
    arguments {
        // Room specific arguments
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
        
        // Dagger/Hilt specific arguments
        arg("dagger.hilt.disableModulesHaveInstallInCheck", "true")
        arg("dagger.fastInit", "enabled")
        arg("dagger.hilt.android.internal.projectType", "app")
        arg("dagger.hilt.internal.useAggregatingRootProcessor", "true")
    }
    
    javacOptions {
        option("-Xmaxerrs", 500)
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("io.card:android-sdk:5.5.1")
    
    // CameraX components
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    // Coroutines Guava integration for ListenableFuture support
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3")
    
    // ML Kit Text Recognition
    implementation("com.google.mlkit:text-recognition:16.0.0")
    
    // Material Design components
    implementation("com.google.android.material:material:1.11.0")

    // CardView for manual entry layout
    implementation("androidx.cardview:cardview:1.0.0")
    
    implementAll(Dependencies.implementations)
    implementAll(SupportDependencies.supportImplementation)
    implementAll(AnnotationProcessors.RegularImplementation) // Add SQLite JDBC here
    
    testImplementAll(TestDependencies.testImplementation)
    testImplementation("org.hamcrest:hamcrest:2.2")
    testAndroidImplementAll(AndroidTestDependencies.androidTestImplementation)
    // Use only KSP for Moshi code generation
    ksp("com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshiKotlin}")
    // KAPT is still needed for Room, Hilt, and other annotation processors
    kaptImplementAll(AnnotationProcessors.AnnotationProcessorsImplementation)
    kaptAndroidTestImplementAll(AnnotationProcessors.AnnotationProcessorsImplementation)
    debugImplementationAll(DebugDependencies.debugImplementation)
    
    // Test orchestrator dependencies
    androidTestUtil("androidx.test:orchestrator:1.4.2")
    
    // Additional test dependencies for API matrix testing
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    
    // Testing Dependencies
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("org.mockito:mockito-android:5.7.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    
    // Debug implementation
    debugImplementation("androidx.fragment:fragment-testing:1.6.2")
    debugImplementation("androidx.test:core:1.5.0")
    
    // JaCoCo coverage agent
    debugImplementation("org.jacoco:org.jacoco.agent:0.8.10:runtime")
}
