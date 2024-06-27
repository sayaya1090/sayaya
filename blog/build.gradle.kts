plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
}
dependencies {
    implementation(project(":activity"))
    implementation(project(":search"))
    implementation(project(":blog-data"))
    implementation(project(":post-data"))
    implementation(libs.spring.log4j2)
    implementation(libs.kotlin.jackson)
    implementation(libs.spring.thymeleaf)
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.r2dbc.postgres)
    testImplementation(libs.bundles.test.api)
    testImplementation(libs.spring.security.test)
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