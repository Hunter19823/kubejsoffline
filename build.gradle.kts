import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
    id("net.neoforged.moddev") version "2.0.138"
    id("com.almostreliable.almostgradle") version "2.2.0"
    id("idea")
    id("com.gradleup.shadow") version "9.0.2"
    id("me.shedaniel.unified-publishing") version "0.1.13"
}

val runningInCI = System.getenv("CI").toBoolean()
val env = System.getenv()

val shadowBundle: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

almostgradle.setup {
    javaVersion = 21
    modPackage = "pie.ilikepiefoo.kubejsoffline"

    launchArgs {
        loggingLevel = "INFO"
    }

    dataGen = false
    splitRunDirs = true
    withAccessTransformerValidation = !runningInCI
}

neoForge {
    interfaceInjectionData {
        from(file("interfaces.json"))
        publish(file("interfaces.json"))
    }
}

repositories {
    mavenCentral()

    maven {
        setUrl("https://maven.neoforged.net/releases")
    }

    maven {
        setUrl("https://maven.latvian.dev/releases")
        content {
            includeGroup("dev.latvian.mods")
            includeGroup("dev.latvian.apps")
        }
    }

    maven {
        setUrl("https://maven.architectury.dev/")
        content {
            includeGroup("dev.architectury")
        }
    }

    maven {
        url = uri("https://maven.pkg.github.com/Hunter19823/kubejsoffline-core")
        credentials {
            username = System.getenv("PACKAGE_ACTOR") ?: ""
            password = System.getenv("PACKAGE_TOKEN") ?: ""
        }
        content {
            includeGroup("pie.ilikepiefoo")
        }
    }

    maven {
        setUrl("https://jitpack.io")
        content {
            includeGroup("com.github.rtyley")
        }
    }

    mavenLocal()
}

dependencies {
    api("dev.latvian.mods:kubejs-neoforge:${property("kubejsVersion")}")
    interfaceInjectionData("dev.latvian.mods:kubejs-neoforge:${property("kubejsVersion")}")

    api("dev.architectury:architectury-neoforge:${property("architecturyVersion")}")

    api("pie.ilikepiefoo:kubejsoffline-core:${property("coreVersion")}")
    compileOnly("org.reflections:reflections:${property("reflectionsVersion")}")

    shadowBundle("pie.ilikepiefoo:kubejsoffline-core:${property("coreVersion")}")
    shadowBundle("org.reflections:reflections:${property("reflectionsVersion")}")
}

fun gitCommitHash(): String {
    return try {
        providers.exec {
            commandLine("git", "rev-parse", "HEAD")
        }.standardOutput.asText.get().trim()
    } catch (_: Exception) {
        project.findProperty("githubCommitHash")?.toString() ?: "unknown"
    }
}

fun documentationResourceProperties(): Map<String, String> {
    return mapOf(
        "mod_id" to property("modId").toString(),
        "mod_name" to property("modName").toString(),
        "mod_version" to property("modVersion").toString(),
        "mod_description" to property("modDescription").toString(),
        "mod_homepage" to property("modHomepage").toString(),
        "mod_source" to property("modSource").toString(),
        "mod_author" to property("modAuthor").toString(),
        "curseforge_id" to property("curseforgeId").toString(),
        "modrinth_id" to property("modrinthId").toString(),
        "minecraft_version" to property("minecraftVersion").toString(),
        "fabric_loader_version" to "N/A",
        "fabric_api_version" to "N/A",
        "neoforge_version" to property("neoforgeVersion").toString(),
        "kubejs_version" to property("kubejsVersion").toString(),
        "architectury_version" to property("architecturyVersion").toString(),
        "github_commit_hash" to gitCommitHash(),
        "core_version" to property("coreVersion").toString(),
    )
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("html/js/constants.js") {
        expand(documentationResourceProperties())
    }
}

tasks.register<ShadowJar>("depsShadowJar") {
    group = "build"
    description = "Bundles mod, kubejsoffline-core, and reflections with relocations"
    dependsOn(tasks.named("compileJava"))
    configurations = listOf(shadowBundle)
    from(tasks.named<JavaCompile>("compileJava").flatMap { it.destinationDirectory })
    archiveClassifier.set("dev-shadow")

    exclude("javax/")
    exclude("org/slf4j/")
    exclude("org/opentest4j/")
    exclude("org/junit/")
    exclude("org/apiguardian/")
    exclude("javassist/tools/")
    exclude("com/google/gson/")
    exclude("org/apache/logging/")
    exclude("META-INF/maven/com.google.code.findbugs/")
    exclude("META-INF/maven/com.google.code.gson/")
    exclude("META-INF/maven/org.apache.logging.log4j/")
    exclude("META-INF/maven/org.slf4j/")
    exclude("META-INF/maven/org.javassist/")
    exclude("META-INF/maven/org.reflections/")
    exclude("META-INF/org/")
    exclude("META-INF/services/")
    exclude("META-INF/DEPENDENCIES")
    exclude("META-INF/junit*")
    exclude("META-INF/LICENSE*")
    exclude("META-INF/NOTICE")
    exclude("Log4j*")

    relocate("org.reflections", "pie.ilikepiefoo.kubejsoffline.reflections")
    relocate("javassist", "pie.ilikepiefoo.kubejsoffline.javassist")
}

val prepareDevShadowRuntime = tasks.register<Sync>("prepareDevShadowRuntime") {
    group = "build"
    description = "Expands shadow dependencies into main output for ModDev (MDG loads classes dirs, not the jar task output)"
    dependsOn("depsShadowJar")
    from(tasks.named<ShadowJar>("depsShadowJar").flatMap { it.archiveFile.map(::zipTree) })
    into(layout.buildDirectory.dir("devShadowRuntime/classes"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val clearNonShadowMainClasses = tasks.register<Delete>("clearNonShadowMainClasses") {
    group = "build"
    description = "Removes pre-relocation mod classes so dev/runtime loads shadow output only"
    dependsOn(prepareDevShadowRuntime)
    delete(sourceSets.main.get().java.destinationDirectory)
}

clearNonShadowMainClasses.configure {
    mustRunAfter(tasks.named("compileJava"))
}

sourceSets.main.get().output.dir(
    layout.buildDirectory.dir("devShadowRuntime/classes"),
    "builtBy" to prepareDevShadowRuntime,
)

tasks.named("classes") {
    dependsOn(prepareDevShadowRuntime, clearNonShadowMainClasses)
}

neoForge {
    runs.configureEach {
        taskBefore(prepareDevShadowRuntime)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    dependsOn(prepareDevShadowRuntime)
    from(sourceSets.main.get().output)
    from(tasks.named("jarJar"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.configureEach {
    if (name.startsWith("run")) {
        dependsOn(tasks.named("classes"))
    }
}

tasks.register<Copy>("updateDocumentation") {
    group = "documentation"
    description = "Moves the generated documentation to the docs directory"

    val minecraftVersion = project.property("minecraftVersion").toString()
    val sourceDoc = layout.projectDirectory.file("runs/client/kubejs/documentation/index.html")
    val targetDoc = layout.projectDirectory.file("docs/$minecraftVersion/neoforge/index.html")

    doLast {
        val source = sourceDoc.asFile
        val target = targetDoc.asFile
        if (!source.exists()) {
            logger.lifecycle("Documentation file does not exist at ${source.absolutePath}")
            return@doLast
        }
        target.parentFile.mkdirs()
        Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        logger.lifecycle("Moved documentation file to ${target.absolutePath}")
    }
}

unifiedPublishing {
    project {
        releaseType = property("uploadType").toString()
        gameVersions = property("supportedVersions").toString().split(", ").toList()
        gameLoaders = listOf("neoforge")
        displayName = "${property("modName")} NeoForge ${project.version}"
        mainPublication(tasks.jar.get())

        relations {
            depends {
                curseforge = "architectury-api"
                modrinth = "architectury-api"
            }
            depends {
                curseforge = "rhino"
                modrinth = "rhino"
            }
            depends {
                curseforge = "kubejs"
                modrinth = "kubejs"
            }
        }

        if (env["CURSEFORGE_KEY"] != null) {
            curseforge {
                token = env["CURSEFORGE_KEY"]
                id = property("curseforgeId").toString()
            }
        }

        if (env["MODRINTH_TOKEN"] != null) {
            modrinth {
                token = env["MODRINTH_TOKEN"]
                id = property("modrinthId").toString()
                version = "${property("modId")}-neoforge-${property("modVersion")}"
            }
        }
    }
}