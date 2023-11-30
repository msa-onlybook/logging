import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.20"
	kotlin("plugin.spring") version "1.9.20"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"


java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}


dependencies {

	//micrometer 추가
	val braveVersion = "1.1.6"
	implementation("io.micrometer:micrometer-tracing-bridge-brave:$braveVersion")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	//macos aarch_64용 netty native 추가
	if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
		implementation("io.netty:netty-resolver-dns-native-macos:4.1.90.Final:osx-aarch_64")
	}

	//webflux 추가
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	//coroutine 추가
	val coroutineVersion = "1.7.3"
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutineVersion}")
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${coroutineVersion}")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:${coroutineVersion}")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

//upstream git command
//git remote add upstream
