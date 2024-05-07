plugins {
    kotlin("jvm")
    id("net.sayaya.gwt")
}
dependencies {
    implementation(libs.bundles.gwt)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
}
tasks {
    jar {
        from(sourceSets.main.get().allSource)
    }
    gwtTest {
        launcherDir = file("src/test/webapp")
    }
}