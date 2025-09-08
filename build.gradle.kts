import java.net.URI

plugins {
	id("maven-publish")
	id("fabric-loom") version "1.11.7"
	id("babric-loom-extension") version "1.10.2"
}

//noinspection GroovyUnusedAssignment
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

base.archivesName = project.properties["archives_base_name"] as String
version = project.properties["mod_version"] as String
group = project.properties["maven_group"] as String

loom {
//	accessWidenerPath = file("src/main/resources/gcapi3.accesswidener") // Unused.

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
	maven("https://maven.glass-launcher.net/snapshots/")
	maven("https://maven.glass-launcher.net/releases/")
	maven("https://maven.glass-launcher.net/babric")
	maven("https://maven.minecraftforge.net/")
	maven("https://jitpack.io/")
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
	mappings("net.glasslauncher:biny:${project.properties["yarn_mappings"]}:v2")
	modImplementation("babric:fabric-loader:${project.properties["loader_version"]}")

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
	modCompileOnly("net.glasslauncher.mods:ModMenu:${project.properties["modmenu_version"]}") {
		isTransitive = false
	}
	implementation("com.google.code.gson:gson:2.13.1")
	modImplementation("net.danygames2014:modmenu:${project.properties["modmenubabric_version"]}") {
		isTransitive = false
	}

	// GCAPI deps
	transitiveImplementation(modImplementation("net.glasslauncher.mods:glass-networking:${project.properties["glass_networking_version"]}") {
		isTransitive = false
	} as Dependency)

	transitiveImplementation(implementation(include("com.google.guava:guava:33.2.1-jre") as Dependency) as Dependency)
	transitiveImplementation(implementation(include("me.carleslc:Simple-Yaml:1.8.4") as Dependency) as Dependency)
}

tasks.withType<ProcessResources> {
	inputs.property("version", project.properties["version"])

	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to project.properties["version"]))
	}
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
