package com.badbones69.crazycrates.files.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Key(val value: String)