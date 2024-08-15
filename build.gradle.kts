plugins {
	kotlin("jvm") version "1.9.24" apply false
}

group = "com.ferick"
version = "1.0"

repositories {
	mavenCentral()
}

subprojects {
	repositories {
		mavenCentral()
	}
	group = rootProject.group
	version = rootProject.version
}
