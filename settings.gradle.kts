rootProject.name = "sayaya"
pluginManagement {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/sayaya1090/maven")
            credentials {
                username = if(settings.extra.has("github_username")) settings.extra["github_username"] as String else System.getenv("GITHUB_USERNAME")
                password = if(settings.extra.has("github_password")) settings.extra["github_password"] as String else System.getenv("GITHUB_TOKEN")
            }
        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("reflect", "org.jetbrains.kotlin", "kotlin-reflect").withoutVersion()
            bundle("kotlin", listOf("reflect"))

            library("spring-webflux", "org.springframework.boot", "spring-boot-starter-webflux").withoutVersion()
            library("kotlin-reactor", "io.projectreactor.kotlin", "reactor-kotlin-extensions").withoutVersion()
            library("kotlin-coroutines-reactor", "org.jetbrains.kotlinx", "kotlinx-coroutines-reactor").withoutVersion()
            library("kotlin-jackson", "com.fasterxml.jackson.module", "jackson-module-kotlin").withoutVersion()
            library("spring-actuator", "org.springframework.boot", "spring-boot-starter-actuator").withoutVersion()
            bundle("kotlin-webflux", listOf("spring-webflux", "reflect", "kotlin-reactor", "kotlin-coroutines-reactor", "kotlin-jackson", "spring-actuator"))

            library("spring-thymeleaf", "org.springframework.boot", "spring-boot-starter-thymeleaf").withoutVersion()
            library("spring-log4j2", "org.springframework.boot", "spring-boot-starter-log4j2").withoutVersion()
            library("spring-security", "org.springframework.boot", "spring-boot-starter-security").withoutVersion()

            library("jjwt-api", "io.jsonwebtoken", "jjwt-api").version { require("0.12.5") }
            library("jjwt-impl", "io.jsonwebtoken", "jjwt-impl").version { require("0.12.5") }
            library("jjwt-jackson", "io.jsonwebtoken", "jjwt-jackson").version { require("0.12.5") }
            library("bouncycastle-bcprov", "org.bouncycastle", "bcprov-jdk18on").version { require("1.78.1") }
            bundle("jjwt-runtime", listOf("jjwt-impl", "jjwt-jackson"))
            library("r2dbc", "org.springframework.boot", "spring-boot-starter-data-r2dbc").withoutVersion()
            library("r2dbc-postgres", "org.postgresql", "r2dbc-postgresql").withoutVersion()
            bundle("r2dbc-postgres", listOf("r2dbc", "r2dbc-postgres"))

            library("spring-cloud-bom", "org.springframework.cloud", "spring-cloud-dependencies").version { require("2023.0.1") }
            library("spring-kubernetes-client", "org.springframework.cloud", "spring-cloud-starter-kubernetes-fabric8").withoutVersion()
            library("spring-kubernetes-config", "org.springframework.cloud", "spring-cloud-starter-kubernetes-fabric8-config").withoutVersion()
            bundle("spring-client", listOf("spring-log4j2", "spring-security"))

            library("jackson-annotations", "com.fasterxml.jackson.core", "jackson-annotations").version { require("2.17.0") }
            library("jackson-databind", "com.fasterxml.jackson.core", "jackson-databind").version { require("2.17.0") }
            library("elemento-core", "org.jboss.elemento", "elemento-core").version { require("1.4.12") }
            library("elemental2-svg", "com.google.elemental2", "elemental2-svg").version { require("1.2.1") }
            library("gwt-user", "org.gwtproject", "gwt-user").version { require("2.11.0") }
            library("gwt-dev", "org.gwtproject", "gwt-dev").version { require("2.11.0") }
            bundle("gwt", listOf("elemento-core", "elemental2-svg", "gwt-user"))

            library("dagger-gwt", "com.google.dagger", "dagger-gwt").version { require("2.51.1") }
            library("dagger-compiler", "com.google.dagger", "dagger-compiler").version { require("2.51.1") }

            library("sayaya-ui", "net.sayaya", "ui").version { require("material3-1.4.1") }
            library("sayaya-rx", "net.sayaya", "rx").version { require("1.6") }
            library("sayaya-test", "net.sayaya", "gwt-test").version { require("1.1") }
            library("lombok", "org.projectlombok", "lombok").version { require("1.18.32") }
            bundle("sayaya-web", listOf("elemento-core", "elemental2-svg", "gwt-user", "dagger-gwt", "dagger-compiler", "sayaya-ui", "sayaya-rx", "lombok"))

            library("reactor-test", "io.projectreactor", "reactor-test").withoutVersion()
            library("kotest-runner", "io.kotest", "kotest-runner-junit5").version { require("5.8.1") }
            library("mockk", "io.mockk", "mockk").version { require("1.13.10") }
            library("kotest-extensions-spring", "io.kotest.extensions", "kotest-extensions-spring").version { require("1.1.3") }
            library("kotest-extensions-testcontainers", "io.kotest.extensions", "kotest-extensions-testcontainers").version { require("2.0.2") }
            library("selenium", "org.seleniumhq.selenium", "selenium-java").version { require("4.20.0") }
            library("spring-boot-test", "org.springframework.boot", "spring-boot-starter-test").withoutVersion()
            library("spring-security-test", "org.springframework.security", "spring-security-test").withoutVersion()
            library("kubernetes-mock", "io.fabric8", "kubernetes-server-mock").version { require("6.10.0") }
            library("testcontainers-junit", "org.testcontainers", "junit-jupiter").withoutVersion()
            library("testcontainers-postgresql", "org.testcontainers", "postgresql").withoutVersion()
            library("testcontainers-vault", "org.testcontainers", "vault").withoutVersion()
            bundle("test-api", listOf("reactor-test", "kotest-runner", "mockk", "kotest-extensions-spring", "spring-boot-test"))
            bundle("test-web", listOf("kotest-runner", "selenium", "sayaya-test", "jackson-databind"))
            bundle("test-containers", listOf("testcontainers-junit", "kotest-extensions-testcontainers"))
            bundle("test-kubernetes", listOf("kubernetes-mock"))
        }
    }
}

include("entity")
include("activity")
include("authentication")
include("shell")
include("shell-ui")
include("vault")
include("login")
include("login-ui")
// include("home-ui")
include("blog")
// include("blog-ui")
include("post")
// include("post-ui")
// include("marked")
// include("search")
