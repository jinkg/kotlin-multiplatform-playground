package com.kinglloy.multiplatform

import androidx.compose.runtime.Composable
import com.kinglloy.multiplatform.di.appModule
import com.kinglloy.multiplatform.di.platformModule
import com.kinglloy.multiplatform.ui.Main
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration

@Composable
@Preview
fun App(activityCloser: ActivityCloser? = null, appDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(
        application = {
            appDeclaration?.invoke(this)
            modules(appModule + platformModule)
        }
    ) {
        Main(activityCloser)
    }
}