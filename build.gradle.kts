plugins {
    id("java")
    kotlin("jvm") version "2.0.0" apply false
    kotlin("kapt") version "2.0.0" apply false
    kotlin("plugin.spring") version "2.0.0" apply false
    id("org.springframework.boot") version "3.2.5" apply false
    id("io.spring.dependency-management") version "1.1.5" apply false
    id("com.google.cloud.tools.jib") version "3.4.2" apply false
    id("net.sayaya.gwt") version "1.1.30" apply false
}
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21
subprojects {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/sayaya1090/maven")
            credentials {
                username = project.findProperty("github_username") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("github_password") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
        mavenCentral()
    }
    group = "net.sayaya"
    version = "2.0"
}