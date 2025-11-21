plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "my.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "my.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // jsoup HTML parser library @ https://jsoup.org/
    implementation("org.jsoup:jsoup:1.21.2")

    //telegram bots
    //ficar ouvindo
    implementation("org.telegram:telegrambots-longpolling:9.2.0")

    //enviar mensagens
    implementation("org.telegram:telegrambots-client:9.2.0")

    //para requisicao já que o android não usa o java completo
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    //jackson
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")

    //para o wrolmanager
    implementation("androidx.work:work-runtime:2.9.0")
}
