plugins {
	`java-library`
}

group = "$GROUP"
version = "0.1.0"

java {
	toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

dependencies {
	// If your plugin depends on core, keep this line. Otherwise remove it.
	api("net.semweb.skein:core")
	testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
}

tasks.test { useJUnitPlatform() }
