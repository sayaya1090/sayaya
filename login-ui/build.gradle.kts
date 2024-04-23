plugins {
    kotlin("jvm")
    id("war")
    id("net.sayaya.gwt")
}
dependencies {
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
tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    gwt {
        gwt.modules = listOf("net.sayaya.Login")
        minHeapSize = "1024M"
        maxHeapSize = "2048M"
        sourceLevel = "auto"
    }
    compileGwt {
        extraJvmArgs = listOf("-javaagent:${lombok}=ECJ")
    }
    gwtDev {
        modules = listOf("net.sayaya.Login")
        extraJvmArgs = listOf("-javaagent:${lombok}=ECJ")
        port = 10010
        codeServerPort = 10011
        war = file("src/main/resources/static")
    }
    gwtTest {
        dependsOn("compileTestJava")
        modules = listOf("net.sayaya.Console")
        launcherDir = file("src/test/resources/static")
        extraJvmArgs = listOf("-javaagent:${lombok}=ECJ")
        webserverPort = 8080
        port = 8081
        src += files(
            File("src/test/java"),
            File("build/generated/sources/annotationProcessor/java/test")
        )
    }
    withType<War> {
        archiveFileName.set("login-ui.war")
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}
