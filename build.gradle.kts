// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.com.dagger.hilt) apply false
    alias(libs.plugins.com.google.ksp) apply false
    alias(libs.plugins.nl.littlerobots.catalog.update)
    alias(libs.plugins.com.github.ben.manes)
    alias(libs.plugins.org.jetbrains.kt.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.services) apply false
}