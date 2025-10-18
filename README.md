# Skein Workspace

This workspace lets any developer quickly scaffold **plugins** and **distributions** using **Gradle Source Dependencies**.

## Requirements
- Java 21+
- Gradle 8.7+ (or use your local wrapper; a wrapper is not bundled)

## Quick start

### Create a new plugin (module)
```bash
cd skein-workspace
./gradlew newModule -Pname=plugin-hello -Pgroup=net.semweb.skein -Ppkg=net.semweb.skein.plugins.hello
```
Then:
```bash
cd ../plugin-hello
./gradlew build
```

### Create a new distribution (explicit list of modules)
```bash
cd skein-workspace
./gradlew newDistribution -Pname=dist-hello -Pmodules=core,plugin-hello
```
Edit the generated `settings.gradle.kts` to point at your real Git URLs
(`git@github.com:YOUR_ORG/<module>.git`), and adjust versions in `gradle/libs.versions.toml` if needed.

Build & run the distribution:
```bash
cd ../dist-hello
./gradlew :app:shadowJar
./gradlew :app:run
```

### Local dev with uncommitted changes
If you keep sibling checkouts like `../core` or `../plugin-hello`, the distribution
template auto-`includeBuild(..)` wires them in, so you can run without committing.

---
**Default group:** `net.semweb.skein`
