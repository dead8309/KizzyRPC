plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation (libs.kotlinx.coroutine)
    implementation (libs.bundles.network.ktor)
    implementation (libs.ktor.websockets)
}
