
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.compose.compiler)
    // ERRO CORRIGIDO: O plugin do Compose foi removido daqui. O buildFeatures { compose = true } já cuida disso de forma mais moderna.

    // Plugin do Google Services para conectar com o Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.calendario"
    compileSdk = 36 // Se 36 estiver dando erro, use 34, que é a versão estável atual

    defaultConfig {
        applicationId = "com.example.calendario"
        minSdk = 24
        targetSdk = 36 // Se 36 estiver dando erro, use 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        // CORREÇÃO: Java 17 é o recomendado para as versões mais novas do Android Gradle Plugin
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        // CORREÇÃO: "17" correspondente ao Java 17
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    // Adicionar o composeOptions para apontar para a versão do compilador do Kotlin
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14" // Versão compatível com Kotlin 1.9.24
    }
}

// ... (seção de plugins e android fica igual) ...

dependencies {
    // BOM do Firebase para gerenciar versões
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Dependências do Firebase que você realmente precisa
    // A BOM vai escolher a versão certa para esta!
    implementation("com.google.firebase:firebase-firestore-ktx")
    // Você também vai precisar da autenticação em breve:
    // implementation("com.google.firebase:firebase-auth-ktx")

    // Dependências padrão do AndroidX e Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

