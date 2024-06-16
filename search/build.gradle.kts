plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}
dependencies {
    implementation(libs.spring.webflux)
    implementation(libs.bundles.r2dbc.postgres)
}