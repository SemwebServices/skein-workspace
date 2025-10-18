pluginManagement {
	repositories { gradlePluginPortal(); mavenCentral() }
}
dependencyResolutionManagement {
	repositories { mavenCentral() }
}
rootProject.name = "skein-workspace"

// Optional quality-of-life: if a dev drops local repos next to workspace,
// auto-wire them for cross-project IDE work (no commits needed).
listOf("../core")
	.plus(file("../").listFiles()?.filter { it.name.startsWith("plugin-") }?.map { it.path } ?: emptyList())
	.map(::file)
	.filter { it.isDirectory }
	.forEach { includeBuild(it) }
