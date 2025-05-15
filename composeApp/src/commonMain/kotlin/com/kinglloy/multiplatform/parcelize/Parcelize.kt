package com.kinglloy.multiplatform.parcelize

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Parcelize()

expect interface Parcelable