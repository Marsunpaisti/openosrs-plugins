import ProjectVersions.openosrsVersion

buildscript {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    checkstyle
    java
    id("com.github.ben-manes.versions") version "0.36.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.15"
    id("com.simonharrer.modernizer") version "2.1.0-1" apply false
}

project.extra["GithubUrl"] = "https://github.com/rokaHakor/openosrs-plugins"
apply<BootstrapPlugin>()
apply<VersionPlugin>()

allprojects {
    group = "com.openosrs.externals"
    apply<MavenPublishPlugin>()
}

subprojects {
    var subprojectName = name
    group = "com.openosrs.externals"

    project.extra["PluginProvider"] = "Satoshi Oda"
    project.extra["ProjectUrl"] = "https://github.com/rokaHakor/openosrs-plugins"
    project.extra["ProjectSupportUrl"] = "https://discord.gg/qPrKNJvBK5"
    project.extra["PluginLicense"] = "3-Clause BSD License"

    repositories {
        jcenter {
            content {
                excludeGroupByRegex("com\\.openosrs.*")
                excludeGroupByRegex("com\\.runelite.*")
            }
        }

        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://repo.runelite.net")
                }
            }
            filter {
                includeModule("net.runelite", "discord")
                includeModule("net.runelite.jogl", "jogl-all")
                includeModule("net.runelite.gluegen", "gluegen-rt")
            }
        }

        exclusiveContent {
            forRepository {
                mavenLocal()
            }
            filter {
                includeGroupByRegex("com\\.openosrs.*")
            }
        }
    }

    apply<JavaPlugin>()
    apply(plugin = "checkstyle")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "se.patrikerdes.use-latest-versions")
    apply(plugin = "com.simonharrer.modernizer")

    dependencies {
        annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.12")
        annotationProcessor(group = "org.pf4j", name = "pf4j", version = "3.4.1")
        implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
        implementation(group = "com.google.code.gson", name = "gson", version = "2.8.6")
        implementation(group = "com.google.guava", name = "guava", version = "29.0-jre")
        implementation(group = "com.google.inject", name = "guice", version = "4.2.3", classifier = "no_aop")
        implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "4.9.0")
        implementation(group = "io.reactivex.rxjava3", name = "rxjava", version = "3.0.6")
        implementation(group = "net.sf.jopt-simple", name = "jopt-simple", version = "5.0.4")
        implementation(group = "org.apache.commons", name = "commons-text", version = "1.9")
        implementation(group = "org.pf4j", name = "pf4j", version = "3.4.1")
        implementation(group = "org.projectlombok", name = "lombok", version = "1.18.12")
        implementation(group = "org.pushing-pixels", name = "radiance-substance", version = "2.5.1")
        compileOnly(group = "com.squareup.okhttp3", name = "okhttp", version = "4.9.1")

        compileOnly("com.openosrs:runelite-api:$openosrsVersion+")
        compileOnly("com.openosrs.rs:runescape-api:$openosrsVersion+")
        compileOnly("com.openosrs:runelite-client:$openosrsVersion+")
        compileOnly("com.openosrs:http-api:$openosrsVersion+")

        compileOnly(Libraries.guice)
        compileOnly(Libraries.javax)
        compileOnly(Libraries.lombok)
        compileOnly(Libraries.pf4j)
    }

    checkstyle {
        maxWarnings = 0
        toolVersion = "8.25"
        isShowViolations = true
        isIgnoreFailures = false
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                url = uri("$buildDir/repo")
            }
        }
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])
            }
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

//        withType<Jar> {
//            doLast {
//                copy {
//                    from("./build/libs/")
//                    into("../release/")
//                }
//            }
//        }

        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            dirMode = 493
            fileMode = 420
        }

        withType<Checkstyle> {
            group = "verification"

            exclude("**/ScriptVarType.java")
            exclude("**/LayoutSolver.java")
            exclude("**/RoomType.java")
        }

        named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
            checkForGradleUpdate = false

            resolutionStrategy {
                componentSelection {
                    all {
                        if (candidate.displayName.contains("fernflower") || isNonStable(candidate.version)) {
                            reject("Non stable")
                        }
                    }
                }
            }
        }

        register<Copy>("copyDeps") {
            into("./build/deps/")
            from(configurations["runtimeClasspath"])
        }
    }
}

tasks {
    named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
        checkForGradleUpdate = false

        resolutionStrategy {
            componentSelection {
                all {
                    if (candidate.displayName.contains("fernflower") || isNonStable(candidate.version)) {
                        reject("Non stable")
                    }
                }
            }
        }
    }
}

fun isNonStable(version: String): Boolean {
    return listOf("ALPHA", "BETA", "RC").any {
        version.toUpperCase().contains(it)
    }
}
