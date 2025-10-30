// Top-level build file

plugins {
    // As declarações 'alias' são o método moderno e correto.
    // 'apply false' significa que o plugin é disponibilizado para os módulos, mas não aplicado no projeto raiz.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false

}
