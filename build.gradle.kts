plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.buildconfig)
}

group = "ua.nanit"
version = "1.12.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.logback.classic)
    implementation(libs.configurate.yaml)

    implementation(libs.netty.handler)
    implementation(variantOf(libs.netty.transport.native.epoll) { classifier("linux-x86_64") })
    implementation(variantOf(libs.netty.transport.native.epoll) { classifier("linux-aarch_64") })
    implementation(variantOf(libs.netty.transport.native.io.uring) { classifier("linux-x86_64") })
    implementation(variantOf(libs.netty.transport.native.io.uring) { classifier("linux-aarch_64") })
    implementation(variantOf(libs.netty.transport.native.kqueue) { classifier("osx-x86_64") })
    implementation(variantOf(libs.netty.transport.native.kqueue) { classifier("osx-aarch_64") })

    implementation(libs.kyori.adventure.api)
    implementation(libs.kyori.adventure.text.serializer.gson)
    implementation(libs.kyori.adventure.text.serializer.legacy)
    implementation(libs.kyori.adventure.text.serializer.json.legacy.impl)
    implementation(libs.kyori.adventure.text.serializer.plain)
    implementation(libs.kyori.adventure.text.serializer.minimessage)
    implementation(libs.kyori.adventure.nbt)

    implementation(libs.gson)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.caffeine)
    implementation(libs.mariadb.java.client)
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.build {
    dependsOn("shadowJar")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    disableAutoTargetJvm()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = Charsets.UTF_8.name()
    options.release = 25
}

buildConfig {
    className("BuildConfig")
    packageName("ua.nanit.limbo")
    buildConfigField("LIMBO_VERSION", provider { "${project.version}" })
}

tasks {
    jar {
        enabled = false
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("NanoLimbo.jar")
        from("LICENSE")

        manifest {
            attributes(
                mapOf(
                    "Main-Class" to "ua.nanit.limbo.NanoLimbo"
                )
            )
        }

//        minimize {
//            exclude(dependency("ch.qos.logback:logback-classic:.*:.*"))
//        }
    }
}