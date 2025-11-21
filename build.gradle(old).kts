


plugins {
    id("java")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val androidHome = System.getenv("ANDROID_HOME") ?: "/home/eliezer/Android/Sdk"
val platform = "$androidHome/platforms/android-34"
val buildTools = "$androidHome/build-tools/34.0.0"

dependencies {
    val androidJar = files("$platform/android.jar")
    compileOnly(androidJar)
}

tasks.register("compileJavaApp", Exec::class) {
    commandLine(
        "javac",
        "--release", "17", // <── força compatibilidade  major version 65
        "-classpath", "$platform/android.jar",
        "-d", "build/classes",
        "src/main/java/org/example/Main.java"
    )
}

//tasks.register("dex", Exec::class) {
//    dependsOn("compileJavaApp")
//
//    doFirst {
//        file("build/dex").mkdirs()
//    }
//
//    commandLine(
//        "$buildTools/d8",
//        "--output", "build/dex",
//        "build/classes"
//    )
//}

//ok funcionando
tasks.register<Exec>("dex") {
    dependsOn("compileJavaApp")

    doFirst {
        file("build/dex").mkdirs()

        // Executa o jar com ProcessBuilder (sem depender do escopo do Gradle)
        val process = ProcessBuilder(
            "jar", "cf", "build/classes.jar", "-C", "build/classes", "."
        )
            .inheritIO()
            .start()
        process.waitFor()
    }

    commandLine(
        "$buildTools/d8",
        "--output", "build/dex",
        "build/classes.jar"
    )
}



tasks.register("buildApk", Exec::class) {
    dependsOn("dex")
    commandLine("zip", "-r", "build/MiniApp.apk", "AndroidManifest.xml", "build/dex/classes.dex")
}


tasks.register("installApk", Exec::class) {
    dependsOn("buildApk")
    commandLine("adb", "install", "-r", "build/MiniApp.apk")
}

tasks.register("runApk", Exec::class) {
    dependsOn("installApk")
    commandLine("adb", "shell", "am", "start", "-n", "org.example/.Main")
}
