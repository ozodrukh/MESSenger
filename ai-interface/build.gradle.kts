plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
}

apply(from = "$rootDir/common.gradle")

android {
    namespace = "com.ozodrukh.llm.api"
}

dependencies {
    implementation(libs.timber)
    implementation(project(":core"))

    implementation(libs.androidx.core.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
}