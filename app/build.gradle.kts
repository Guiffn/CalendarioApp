plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.compose.compiler)
    // ERRO CORRIGIDO: O plugin do Compose foi removido daqui.
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


dependencies {
    // BOM do Firebase para gerenciar versões
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // --- CORREÇÃO DEFINITIVA DAS DEPENDÊNCIAS ---
    // Eu tinha errado os nomes. Estes são os corretos (com hifens):
    implementation("io.github.boguszpawlowski:compose-calendar:1.3.1")
    implementation("io.github.boguszpawlowski:compose-calendar-kotlinx-datetime:1.3.1")


    // Dependências padrão do AndroidX e Compose
    implementation(libs.androidx.core.ktx) // <--- Sem duplicata

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