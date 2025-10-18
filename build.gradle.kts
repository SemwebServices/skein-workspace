import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

tasks.register("newModule") {
	group = "workspace"
	description = "Scaffold a new plugin repo"
	doLast {
		val name = (project.findProperty("name") as String?) ?: error("Pass -Pname=plugin-foo")
		val group = (project.findProperty("group") as String?) ?: "net.semweb.skein"
		val pkg   = (project.findProperty("pkg") as String?) ?: "${group}.plugins.${name.removePrefix("plugin-")}"
		val dst = project.rootDir.toPath().resolve("../$name")
		require(!dst.toFile().exists()) { "Target $dst already exists" }
		copyTree(project.rootDir.toPath().resolve("templates/module"), dst)
		rewrite(dst.resolve("build.gradle.kts")) {
			it.replace("\$GROUP", group).replace("\$NAME", name)
		}
		val marker = dst.resolve("src/main/java/com/example/PLUGIN/PluginMarker.java")
		val pkgPath = dst.resolve("src/main/java/" + pkg.replace('.', '/'))
		Files.createDirectories(pkgPath)
		Files.move(marker, pkgPath.resolve("PluginMarker.java"), StandardCopyOption.REPLACE_EXISTING)
		rewrite(pkgPath.resolve("PluginMarker.java")) { it.replace("com.example.PLUGIN", pkg) }
		println("✔ Created $name at $dst")
		println("Next: cd $name && ./gradlew build")
	}
}

tasks.register("newDistribution") {
	group = "workspace"
	description = "Scaffold a new distribution repo (source dependencies)"
	doLast {
		val name = (project.findProperty("name") as String?) ?: error("Pass -Pname=dist-foo")
		val modules = (project.findProperty("modules") as String? ?: "core,plugin-foo")
			.split(",").map { it.trim() }.filter { it.isNotEmpty() }
		val dst = project.rootDir.toPath().resolve("../$name")
		require(!dst.toFile().exists()) { "Target $dst already exists" }
		copyTree(project.rootDir.toPath().resolve("templates/distribution"), dst)
		// Source control mapping blocks
		val sc = buildString {
			appendLine("sourceControl {")
			modules.forEach { mod ->
				val url = "git@github.com:YOUR_ORG/$mod.git" // adjust to your org
				appendLine("\tgitRepository(\"$url\") {")
				appendLine("\t\tproducesModule(\"net.semweb.skein:${mod}\")")
				appendLine("\t}")
			}
			appendLine("}")
		}
		appendAfter(dst.resolve("settings.gradle.kts"), marker = "//__SOURCE_CONTROL__", appendix = sc)
		// Version catalog
		val libsToml = dst.resolve("gradle/libs.versions.toml")
		val lines = buildString {
			appendLine("[versions]")
			modules.forEach { appendLine("${it.replace('-', '_')} = \"0.1.0\"") }
			appendLine()
			appendLine("[libraries]")
			modules.forEach { m ->
				appendLine("${m.replace('-', '_')} = { group = \"net.semweb.skein\", name = \"$m\", version.ref = \"${m.replace('-', '_')}\" }")
			}
			appendLine()
			appendLine("[bundles]")
			appendLine("skein_all = [${modules.joinToString(", ") { "\"${it.replace('-', '_')}\"" }}]")
		}
		Files.writeString(libsToml, lines)
		// App dependencies
		val appBuild = dst.resolve("app/build.gradle.kts")
		val deps = modules.joinToString("\n") { "\timplementation(libs.${it.replace('-', '_')})" }
		appendAfter(appBuild, "//__DEPENDENCIES__", deps)
		println("✔ Created $name at $dst")
		println("Next: cd $name && ./gradlew :app:shadowJar")
	}
}

fun copyTree(from: Path, to: Path) {
	Files.walk(from).forEach { p ->
		val rel = from.relativize(p)
		val tgt = to.resolve(rel.toString())
		if (p.toFile().isDirectory) Files.createDirectories(tgt) else Files.copy(p, tgt, StandardCopyOption.REPLACE_EXISTING)
	}
}
fun rewrite(path: Path, f: (String) -> String) {
	Files.writeString(path, f(Files.readString(path)))
}
fun appendAfter(path: Path, marker: String, appendix: String) {
	val orig = Files.readString(path)
	val updated = orig.replace(marker, marker + "\n" + appendix)
	Files.writeString(path, updated)
}
