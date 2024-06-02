plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.adarshr.test-logger")
}
dependencies {
    implementation(libs.spring.webflux)
    implementation(libs.kotlin.jackson)
    testImplementation(libs.bundles.test.api)
    testImplementation(libs.bundles.test.containers)
    testImplementation(libs.testcontainers.vault)
}
tasks.test {
    useJUnitPlatform()
}