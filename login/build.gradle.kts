plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
}
dependencies {
    implementation(project(":vault"))
    implementation(libs.bundles.spring.client)
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation(libs.kotlin.jackson)
    implementation(libs.bundles.kotlin.webflux)
    implementation(libs.bundles.r2dbc.postgres)
    implementation(libs.bouncycastle.bcprov)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.bundles.jjwt.runtime)
    testImplementation(libs.bundles.test.api)
    testImplementation(libs.bundles.test.containers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.vault)
    testImplementation(libs.spring.security.test)
}
configurations { all { exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") } }
dependencyManagement { imports { mavenBom(libs.spring.cloud.bom.get().toString()) } }
kapt {
    keepJavacAnnotationProcessors = true
    includeCompileClasspath = false
}
tasks.getByName<Jar>("jar") {
    enabled = false
}
tasks.test {
    useJUnitPlatform()
}
jib {
    container {
        environment = mapOf(
            "LANG" to "C.UTF-8",
            "TZ" to "Asia/Seoul",
        )
    }
}
