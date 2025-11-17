plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.compose.compiler)
// Plugin do Google Services para conectar com o Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.calendario"
    compileSdk = 36 // Se 36 estiver dando erro, use 34, que é a versão estável atual

    defaultConfig {
        applicationId = "com.example.calendario"

        // --- MUDANÇA ---
        // Voltámos ao 24, pois vamos usar o "Desugaring"
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

        // --- MUDANÇA: RE-ADICIONADO ---
        // A nova biblioteca também precisa disto
        isCoreLibraryDesugaringEnabled = true
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

    // --- MUDANÇA: RE-ADICIONADO ---
    // A biblioteca que faz o "Desugaring"
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // BOM do Firebase para gerenciar versões
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // --- MUDANÇA: BIBLIOTECA ANTIGA REMOVIDA ---
    // implementation("com.kizitonwose.calendar:compose:2.5.1")
    // implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    // --- MUDANÇA: NOVA BIBLIOTECA ADICIONADA ---
    // Vamos usar a versão 'kotlinx-datetime' desta biblioteca para evitar o java.time
    implementation("io.github.boguszpawlowski.composecalendar:kotlinx-datetime:1.1.0")


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