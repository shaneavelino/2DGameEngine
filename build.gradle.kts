plugins {
    id("java")
    id("application")
}

group = "org.lavostudio"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("org.lavostudio.Main")
    
    // Add JVM arguments for macOS
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}

repositories {
    mavenCentral()
}

// Add a task to generate placeholder images
tasks.register<JavaExec>("generatePlaceholders") {
    group = "application"
    description = "Generate placeholder images for testing"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.lavostudio.PlaceholderGenerator")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

val lwjglVersion = "3.3.6"
val jomlVersion = "1.10.7"
val lwjglNatives = "natives-macos"

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-nfd")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nfd", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    implementation("org.joml", "joml", jomlVersion)
    
    // Add JetBrains annotations dependency for @NotNull
    implementation("org.jetbrains:annotations:24.1.0")
}