package com.kinglloy.multiplatform.parcelize

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Parcelize()

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class IgnoredOnParcel()

expect interface Parcelable