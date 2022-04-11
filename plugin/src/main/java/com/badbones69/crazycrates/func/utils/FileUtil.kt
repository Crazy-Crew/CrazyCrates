package com.badbones69.crazycrates.func.utils

import java.io.File
import java.io.InputStream

object FileUtil {
    fun copyFile(jarFile: InputStream, file: File) {
        runCatching {
            jarFile.use { inStream ->
                file.outputStream().use { outStream ->
                    val buffer = ByteArray(1024)
                    var count: Int
                    while (inStream.read(buffer).also { count = it } != -1) outStream.write(buffer, 0, count)
                }
            }
        }.onFailure {
            println("Saving ${file.name} failed. \n ${it.message}")
        }
    }
}