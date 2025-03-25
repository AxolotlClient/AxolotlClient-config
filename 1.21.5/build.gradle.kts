@file:Suppress("UnstableApiUsage")

plugins {
	id("java")
	id("fabric-loom")
}

val minecraft = "1.21.5"
val parchmentMinecraft = "1.21.4"
val parchment = "2025.03.23"
val fapiVersion = "0.119.5+1.21.5"
group = project.property("maven_group") as String
version = "${project.property("version")}+1.21.5"
base.archivesName = "AxolotlClient-config"

repositories {
	maven("https://moehreag.duckdns.org/maven/releases")
	maven("https://maven.terraformersmc.com/releases")
	maven("https://maven.parchmentmc.org")
	mavenLocal()
}

loom {
	mods {
		create("axolotlclientconfig") {
			sourceSet("main")
		}
		create("axolotlclientconfig-test") {
			sourceSet("test")
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${minecraft}")
	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-$parchmentMinecraft:$parchment@zip")
	})

	modImplementation("net.fabricmc:fabric-loader:${project.property("fabric_loader")}")

	modImplementation("net.fabricmc.fabric-api:fabric-api:${fapiVersion}")

	modCompileOnlyApi("com.terraformersmc:modmenu:6.1.0-beta.3") {
		isTransitive = false
	}

	implementation(include(project(":common"))!!)

	include(implementation("org.lwjgl:lwjgl-nanovg:3.3.3")!!)
	include(runtimeOnly("org.lwjgl:lwjgl-nanovg:3.3.3:natives-linux")!!)
	include(runtimeOnly("org.lwjgl:lwjgl-nanovg:3.3.3:natives-linux-arm64")!!)
	include(runtimeOnly("org.lwjgl:lwjgl-nanovg:3.3.3:natives-windows")!!)
	include(runtimeOnly("org.lwjgl:lwjgl-nanovg:3.3.3:natives-windows-arm64")!!)
	include(runtimeOnly("org.lwjgl:lwjgl-nanovg:3.3.3:natives-macos")!!)
	include(runtimeOnly("org.lwjgl:lwjgl-nanovg:3.3.3:natives-macos-arm64")!!)
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}

tasks.withType(JavaCompile::class).configureEach {
	options.encoding = "UTF-8"

	if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_22)) {
		options.release = 21
	}
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.runClient {
    classpath(sourceSets.getByName("test").runtimeClasspath)
}

// Configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "owlMaven"
            val repository = if (project.version.toString().contains("beta") || project.version.toString().contains("alpha")) "snapshots" else "releases"
            url = uri("https://moehreag.duckdns.org/maven/$repository")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}
