plugins {
    id("java")
    kotlin("jvm")
}

group = "io.deeplay.camp"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.2.20")
    implementation("org.nd4j:nd4j-native-platform:1.0.0-beta7")
    implementation("org.deeplearning4j:deeplearning4j-core:1.0.0-beta7")
    implementation("org.deeplearning4j:deeplearning4j-core:1.0.0-beta7")
    implementation("org.nd4j:nd4j-native-platform:1.0.0-beta7")
    implementation("com.microsoft.onnxruntime:onnxruntime:1.8.1")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation(project(":reversi-server:model"))
    implementation(project(":utilities"))
    implementation(project(":reversi-server:data-base"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    implementation("org.postgresql:postgresql:42.3.1")
    implementation("org.json:json:20230227")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly ("org.postgresql:postgresql")
    runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly ("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}