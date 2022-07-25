package com.badbones69.crazycrates.utils

import com.badbones69.crazycrates.files.files.NewFileManager
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.JarURLConnection
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile

class FileUtils {

    val dataFolder: Path? = NewFileManager.getDataFolder()?.toPath()
    val localeDir: Path? = dataFolder?.resolve("locale")

    fun extract(inDir: String, outDir: Path, replace: Boolean) {
        val dirURL = javaClass.getResource(inDir)

        if (dirURL == null) println("Cannot find $inDir on the classpath.")

        if (!dirURL?.protocol.equals("jar")) println("Don't know what I am doing.")

        var jar: ZipFile? = null

        runCatching {
            jar = (dirURL?.openConnection() as JarURLConnection).jarFile
        }.onFailure {
            println("Failed to extract directory from jar.")
        }

        val path = inDir.substring(1)

        val entries = jar?.entries()

        while (entries?.hasMoreElements() == true) {
            val entry = entries.nextElement()

            val name = entry.name

            if (!name.startsWith(path)) continue

            val file = outDir.resolve(name.substring(path.length))
            val exists = Files.exists(file)

            if (!replace && exists) {
                println("$name already exists.")
                continue
            }

            if (entry.isDirectory) {
                if (!exists) {
                    runCatching {
                        Files.createDirectories(file)
                        println("Creating $name")
                    }.onFailure {
                        println("Failed to create $name")
                    }
                } else {
                    println("File named $name exists.")
                }

                continue
            }

            runCatching {
                val inJar = jar?.getInputStream(entry)
                val outJar = BufferedOutputStream(FileOutputStream(file.toFile()))
                val buffer = ByteArray(4096)
                var readCount: Int

                while (inJar?.read(buffer).also { readCount = it!! }!! > 0) {
                    outJar.write(buffer, 0, readCount)
                }

                outJar.flush()

                println("Writing $name")
            }.onFailure {
                println("Failed to write $name.")
                println("Can't extract $name from jar.")
            }
        }
    }
}