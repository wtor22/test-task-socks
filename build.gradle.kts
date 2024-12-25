
plugins {
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.1.6"
    id("java")
    id("jacoco")
}


group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Основные зависимости Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web"){
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-data-jpa"){
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-validation"){
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }

    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")



    implementation("org.projectlombok:lombok:1.18.28")

    annotationProcessor("org.projectlombok:lombok:1.18.28")

    // PostgreSQL Driver
    runtimeOnly("org.postgresql:postgresql")

    // Swagger/OpenAPI
    implementation("org.springdoc:springdoc-openapi-ui:1.6.14")

    // Зависимости для тестирования
    testImplementation("org.springframework.boot:spring-boot-starter-test"){
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}


tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.jacocoTestReport {
    reports {
        xml.required = false
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}


jacoco {
    toolVersion = "0.8.12"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}





tasks.test {
    useJUnitPlatform()
}