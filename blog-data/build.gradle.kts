plugins {
    kotlin("jvm")
}
dependencies {
    implementation(libs.bundles.gwt)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
}
tasks {
    jar {
        from(sourceSets.main.get().allSource)
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}