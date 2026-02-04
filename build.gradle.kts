import java.net.URI

plugins {
	id("maven-publish")
	id("net.fabricmc.fabric-loom-remap") version "1.15.+"
	id("ploceus") version "1.15-SNAPSHOT"
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

base.archivesName = project.properties["archives_base_name"] as String
version = project.properties["mod_version"] as String
group = project.properties["maven_group"] as String

ploceus {
	setIntermediaryGeneration(2)
}

loom {
	accessWidenerPath = file("src/main/resources/gcapi3.accesswidener")

	@Suppress("UnstableApiUsage")
	mixin.defaultRefmapName = "${project.properties["archives_base_name"]}3-refmap.json"

	runs {
		register("testClient") {
			source("test")
			client()
		}
		register("testServer") {
			source("test")
			server()
		}
	}
}

repositories {
	mavenLocal()
	maven("https://maven.glass-launcher.net/releases")
	maven("https://mvn.devos.one/releases")
	maven("https://maven.wispforest.io")
	maven("https://maven.glass-launcher.net/snapshots/")
	maven("https://maven.minecraftforge.net/")
	maven("https://jitpack.io/")
    maven("https://maven.ornithemc.net/releases")
	mavenCentral()
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven")
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")
	mappings(ploceus.mappings("net.glasslauncher:biny-ornithe:b1.7.3+build.${project.properties["biny_mappings"]}:mergedv2"))

	"clientExceptions"(ploceus.raven(project.properties["client_raven_build"] as String, "client"))
	"serverExceptions"(ploceus.raven(project.properties["server_raven_build"] as String, "server"))
	"clientSignatures"(ploceus.sparrow(project.properties["client_sparrow_build"] as String, "client"))
	"serverSignatures"(ploceus.sparrow(project.properties["server_sparrow_build"] as String, "server"))
    "clientNests"("net.glasslauncher:biny-nests:b1.7.3-client+build.2")
    "serverNests"("net.glasslauncher:biny-nests:b1.7.3-server+build.2")

	modImplementation("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")

	implementation("org.apache.logging.log4j:log4j-core:2.17.2")

	implementation("org.slf4j:slf4j-api:1.8.0-beta4")
	implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.17.1")

	// convenience stuff
	// adds some useful annotations for data classes. does not add any dependencies
	compileOnly("org.projectlombok:lombok:1.18.24")
	annotationProcessor("org.projectlombok:lombok:1.18.38")

	// adds some useful annotations for miscellaneous uses. does not add any dependencies, though people without the lib will be missing some useful context hints.
	implementation("org.jetbrains:annotations:23.0.0")

	// Optional GCAPI deps
	// TODO: add back when modmenu is on ornithe
    modCompileOnly("com.terraformersmc:modmenu:${project.properties["modmenu_version"]}")

	// GCAPI deps
	implementation(include("com.google.guava:guava:33.2.1-jre")!!)
	implementation(include("me.carleslc:Simple-Yaml:1.8.4")!!)
}

tasks.withType<ProcessResources> {
	inputs.property("version", project.properties["version"])

	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to project.properties["version"]))
	}
}

// Don't fail test task when no tests are discovered (these are mod test classes, not unit tests)
tasks.withType<Test> {
    failOnNoDiscoveredTests = false
}

// Tell gradle to stop trying to be smart.
tasks.withType<GenerateModuleMetadata> {
	enabled = false
}

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.release = 21
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}

tasks.withType<Jar> {
	from("LICENSE") {
		rename { "${it}_${project.properties["archivesBaseName"]}" }
	}
}

publishing {
	repositories {
		mavenLocal()
		if (project.hasProperty("glass_maven_username")) {
			maven {
				url = URI("https://maven.glass-launcher.net/releases")
				credentials {
					username = "${project.properties["glass_maven_username"]}"
					password = "${project.properties["glass_maven_password"]}"
				}
			}
		}
	}

	publications {
		register("mavenJava", MavenPublication::class) {
			artifactId = project.properties["archives_base_name"] as String
			from(components["java"])
		}
	}
}
