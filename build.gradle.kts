// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false

    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false

    id("com.google.gms.google-services") version "4.4.2" apply false
}