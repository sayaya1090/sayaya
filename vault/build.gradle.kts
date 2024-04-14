plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}
dependencies {
    implementation(libs.spring.webflux)
    implementation(libs.kotlin.jackson)
    testImplementation(libs.bundles.test)
    testImplementation(libs.testcontainers.vault)
}
tasks.test {
    useJUnitPlatform()
}