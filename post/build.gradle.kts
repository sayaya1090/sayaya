plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
    id("com.adarshr.test-logger")
}
dependencies {
    implementation(project(":vault"))
    implementation(project(":authentication"))
    implementation(project(":activity"))
    implementation(project(":post-data"))
    implementation(project(":search"))
    implementation(libs.bundles.spring.client)
    implementation(libs.kotlin.jackson)
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.r2dbc.postgres)
    implementation(libs.bouncycastle.bcprov)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.bundles.jjwt.runtime)
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.1.0"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")
    implementation("software.amazon.awssdk:s3-transfer-manager")
    implementation("software.amazon.awssdk.crt:aws-crt")
    testImplementation(libs.bundles.test.api)
    testImplementation(libs.bundles.test.containers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.vault)
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