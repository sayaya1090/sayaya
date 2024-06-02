plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
    id("com.adarshr.test-logger")
}
dependencies {
    implementation(libs.spring.thymeleaf)
    implementation(libs.spring.log4j2)
    implementation(libs.spring.kubernetes.client)
    implementation(libs.kotlin.jackson)
    implementation(libs.bundles.kotlin.webflux)
   // testImplementation(project(":activity"))
    testImplementation(libs.bundles.test.api)
    testImplementation(libs.bundles.test.kubernetes)
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
dependencyManagement { imports { mavenBom(libs.spring.cloud.bom.get().toString()) } }
tasks {
    jar {
        enabled = false
    }
    test {
        useJUnitPlatform()
    }
}
jib {
    container {
        environment = mapOf(
            "LANG" to "C.UTF-8",
            "TZ" to "Asia/Seoul",
        )
    }
}