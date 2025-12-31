plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
}

apply(from = "$rootDir/common.gradle")

android {
    namespace = "com.ozodrukh.auth"
}

dependencies {
    implementation(project(":core"))
    implementation(libs.androidx.core.ktx)

    implementation(libs.retrofit)
    implementation(libs.okhttp)

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
}