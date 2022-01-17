import ProjectVersions.rlVersion

buildscript {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    checkstyle
    java
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

    dependencies {
        compileOnly("com.openosrs:runelite-api:$rlVersion+")
        compileOnly("com.openosrs.rs:runescape-api:$rlVersion+")
        compileOnly("com.openosrs:runelite-client:$rlVersion+")
        compileOnly("com.openosrs:http-api:$rlVersion+")

        compileOnly(group = "org.pf4j", name = "pf4j-update", version = "2.3.0")
        compileOnly(Libraries.okhttp3)
        compileOnly(Libraries.guice)
        compileOnly(Libraries.lombok)
        compileOnly(Libraries.pf4j)
        compileOnly(Libraries.apacheCommonsText)
        compileOnly(Libraries.gson)
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

        register<Copy>("copyDeps") {
            into("./build/deps/")
            from(configurations["runtimeClasspath"])
        }
    }
}

fun isNonStable(version: String): Boolean {
    return listOf("ALPHA", "BETA", "RC").any {
        version.toUpperCase().contains(it)
    }
}
