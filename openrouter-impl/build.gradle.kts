plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
}

apply(from = "$rootDir/common.gradle")

android {
    namespace = "com.ozodrukh.openrouter.impl"
}

dependencies {
    implementation(libs.timber)
    implementation(libs.androidx.core.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)

    implementation("ai.koog:koog-agents:0.6.0")
    implementation(project(":core"))
    implementation(project(":ai-interface"))

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
}