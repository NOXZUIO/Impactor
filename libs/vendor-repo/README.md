# Vendored repo: net.kyori:event-api:5.0.0-SNAPSHOT

## Why this exists

The build was failing with:

```
Could not find net.kyori:event-api:5.0.0-SNAPSHOT.
```

`event-api:5.0.0-SNAPSHOT` was only ever published as a SNAPSHOT to Sonatype's
old OSSRH host (`s01.oss.sonatype.org`). That host has since been decommissioned
as part of Sonatype's migration to the Central Portal, and the upstream project,
[KyoriPowered/event](https://github.com/KyoriPowered/event), was **archived
(read-only) on 2023-07-16** — so nobody can republish it to the new Central
Portal snapshots repo either. As a result this coordinate is not resolvable from
Maven Central, the new `central.sonatype.com/repository/maven-snapshots/`,
Fabric, Architectury, or any other repo already configured in this project.

## What this is

This directory is a minimal flat-file Maven repository containing a jar built
directly from the last commit of the `api` module in the archived
`KyoriPowered/event` source repository, so the build can resolve
`net.kyori:event-api:5.0.0-SNAPSHOT` without any network dependency on
infrastructure that no longer exists.

Contents:
- `net/kyori/event-api/5.0.0-SNAPSHOT/event-api-5.0.0-SNAPSHOT.jar` — compiled classes
- `net/kyori/event-api/5.0.0-SNAPSHOT/event-api-5.0.0-SNAPSHOT-sources.jar` — matching sources
- `net/kyori/event-api/5.0.0-SNAPSHOT/event-api-5.0.0-SNAPSHOT.pom` — minimal POM (no runtime deps)

The only compile-time dependency of the original module,
`org.checkerframework:checker-qual` (used solely for a `@NonNull` annotation,
`compileOnlyApi` scope), was **not** bundled — it was replaced at compile time
with a local, source-retention-only stub annotation, since checker-qual is not
part of the published artifact's runtime or API surface anyway.

Wired in via `build-logic/src/main/kotlin/impactor.base-conventions.gradle.kts`,
which adds this directory as a repository ahead of the remote ones.

## How to refresh / replace this later (recommended)

This is a stop-gap so CI/local builds work today. Long-term, prefer one of:

1. Fork `KyoriPowered/event`, build it yourself, and publish it to your own
   `maven.impactdev.net` repository under a coordinate you control
   (e.g. `net.impactdev:event-api:5.0.0`), then update the dependency string in
   `impactor.launcher-conventions.gradle.kts` accordingly.
2. Vendor the source directly into this repo (e.g. under `libs/event/`) and use
   Gradle's composite build (`includeBuild`) instead of a binary jar, so the
   code is visible/diffable and rebuilt as part of the normal build.

To rebuild this jar from scratch:

```bash
git clone https://github.com/KyoriPowered/event.git
cd event/api/src/main/java
# compile net/kyori/event/*.java, providing a stub for
# org.checkerframework.checker.nullness.qual.NonNull, then jar up the
# net/kyori/event package only (do not include the stub annotation).
```
