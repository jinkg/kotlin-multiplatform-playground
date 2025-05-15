package com.kinglloy.multiplatform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

interface ActivityCloser {
    fun requestClose()
}

