version = "2.2.6"

project.extra["PluginName"] = "PaistiSuite"
project.extra["PluginDescription"] = "Scripting framework by Paisti. Required by all other Paisti Plugins to work!"

dependencies {
    compileOnly(group = "com.openosrs.rs", name = "runescape-api", version = "4.0.0")
}

tasks {
    jar {
        manifest {
            attributes(mapOf(
                    "Plugin-Version" to project.version,
                    "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                    "Plugin-Provider" to project.extra["PluginProvider"],
                    "Plugin-Description" to project.extra["PluginDescription"],
                    "Plugin-License" to project.extra["PluginLicense"]
            ))
        }
    }
}