package com.kinglloy.multiplatform

import androidx.compose.runtime.Composable
import com.kinglloy.multiplatform.di.appModule
import com.kinglloy.multiplatform.ui.Main
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App(activityCloser: ActivityCloser? = null) {
    KoinApplication(
        application = {
            modules(appModule)
        }
    ) {
        Main(activityCloser)
    }
}