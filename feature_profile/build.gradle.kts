plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
}

apply(from = "$rootDir/common.gradle")

android {
    namespace = "com.ozodrukh.feature.profile"
}

dependencies {
    api(libs.timber)
    api(libs.androidx.core.ktx)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.activity.compose)
    api(libs.androidx.compose.material3)
    api(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.icons)

    implementation(libs.androidx.appcompat)
    api(libs.kotlinx.collections.immutable)
    implementation(libs.material)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(project(":auth"))
    implementation(project(":core"))
    implementation(libs.gson)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
}
