plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.6.21"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {

    //Mongo
    implementation("org.litote.kmongo:kmongo:4.7.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.1")
    implementation("com.github.javasync:RxIo:1.2.5")
    implementation("org.litote.kmongo:kmongo-coroutine:4.7.2")

    implementation(project(":checkers-model"))

    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation(project(":checkers-model"))

}

application {
    // Define the main class for the application.
    mainClass.set("pt.isel.AppKt")
}
