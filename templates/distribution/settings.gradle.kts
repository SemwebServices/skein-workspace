pluginManagement {
	repositories { gradlePluginPortal(); mavenCentral() }
}
dependencyResolutionManagement {
	versionCatalogs { create("libs") { from(files("gradle/libs.versions.toml")) } }
	repositories { mavenCentral() /* + your Nexus */ }
}
rootProject.name = "dist-template"

//__SOURCE_CONTROL__   // (generator inserts gitRepository blocks here)

include(":app")

// Optional dev override: use local working copies if present (uncommitted ok)
listOf("../core")
	.plus(file("../").listFiles()?.filter { it.name.startsWith("plugin-") }?.map { it.path } ?: emptyList())
	.map(::file)
	.filter { it.isDirectory }
	.forEach { includeBuild(it) }
