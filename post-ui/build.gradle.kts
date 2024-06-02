plugins {
    kotlin("jvm")
    id("war")
    id("net.sayaya.gwt")
    id("com.adarshr.test-logger")
}
dependencies {
    implementation(project(":activity"))
    implementation(project(":post-data"))
    implementation(project(":card-ui"))
    implementation(project(":marked"))
    implementation(libs.bundles.sayaya.web)
    compileOnly(libs.gwt.dev)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.dagger.compiler)
    testImplementation(libs.bundles.test.web)
    testImplementation(libs.kotlin.jackson)
    testAnnotationProcessor(libs.lombok)
    testAnnotationProcessor(libs.dagger.compiler)
}
val lombok = project.configurations.annotationProcessor.get().filter { it.name.startsWith("lombok") }.single()!!
sourceSets.getByName("main").java.srcDirs(
    "build/generated/sources/annotationProcessor/java/main"
)
gwt {
    gwtVersion = "2.11.0"
    modules = listOf("net.sayaya.Post")
    minHeapSize = "1024M"
    maxHeapSize = "2048M"
    sourceLevel = "auto"
    extraJvmArgs = listOf("-javaagent:${lombok}=ECJ")
    gwt.jsInteropExports.setGenerate(true)
    compiler.strict = true
    compiler.disableClassMetadata = true
    compiler.disableCastChecking = true
}
tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    gwtDev {
        port = 10010
        codeServerPort = 10011
        war = file("src/main/webapp")
    }
    gwtTest {
        dependsOn("compileTestJava")
        modules = listOf("net.sayaya.PostListTest", "net.sayaya.PostEditTest")
        launcherDir = file("src/test/webapp")
        webserverPort = 8080
        port = 8081
        src += files(
            File("src/test/java"),
            File("src/test/resources"),
            File("build/generated/sources/annotationProcessor/java/test")
        )
    }
    withType<War> {
        archiveFileName.set("post-ui.war")
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}