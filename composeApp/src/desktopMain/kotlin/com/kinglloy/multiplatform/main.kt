package com.kinglloy.multiplatform

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "kotlin-multiplatform-playground",
    ) {
        App()
    }
}