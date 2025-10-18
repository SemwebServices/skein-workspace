plugins {
	application
	id("com.gradleup.shadow") version "8.3.0"
}

application {
	mainClass.set("net.semweb.launcher.Main")
}

dependencies {
	//__DEPENDENCIES__   // (generator inserts implementation(libs.*) lines)
}

tasks.shadowJar {
	archiveClassifier.set("")
	mergeServiceFiles()
}

tasks.assemble { dependsOn(tasks.shadowJar) }
