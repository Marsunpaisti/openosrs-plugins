version = "3.1.1"

project.extra["PluginName"] = "AiO Fighter"
project.extra["PluginDescription"] = "Fully configurable all-in-one fighter"

dependencies {
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.12")
    annotationProcessor(group = "org.pf4j", name = "pf4j", version = "3.2.0")
    compileOnly(project(":paistisuite"))
}

tasks {
    jar {
        manifest {
            attributes(mapOf(
                    "Plugin-Version" to project.version,
                    "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                    "Plugin-Provider" to project.extra["PluginProvider"],
                    "Plugin-Dependencies" to nameToId("PaistiSuite"),
                    "Plugin-Description" to project.extra["PluginDescription"],
                    "Plugin-License" to project.extra["PluginLicense"]
            ))
        }
    }
}