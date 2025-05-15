package com.kinglloy.multiplatform

import android.app.Activity
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

class AndroidActivityCloser(private val activity: Activity?) : ActivityCloser {
    override fun requestClose() {
        activity?.finish()
    }
}

@Composable
fun rememberPlatformActivityCloser(): ActivityCloser {
    val context = LocalContext.current
    return remember(context) {
        AndroidActivityCloser(context as? Activity)
    }
}
