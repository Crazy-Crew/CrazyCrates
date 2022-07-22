package com.badbones69.crazycrates.utils

import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.net.JarURLConnection
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile

object FileUtils {

    val dataFolder: Path = PluginUtils.plugin.dataFolder.toPath()
    val localeDir: Path = dataFolder.resolve("locale")

    fun extract(filename: String?, replace: Boolean) {
        val file = Files.exists(dataFolder.resolve(filename.toString()))

        if (file) PluginUtils.plugin.saveResource(filename.toString(), replace)
    }

    fun extract(inDir: String, outDir: Path, replace: Boolean) {
        val dirURL = PluginUtils.javaClass.getResource(inDir)

        if (dirURL == null) PluginUtils.plugin.logger.warning("Cannot find $inDir on the classpath.")

        if (!dirURL?.protocol.equals("jar")) PluginUtils.plugin.logger.warning("Don't know what I am doing.")

        var jar: ZipFile? = null

        runCatching {
            jar = (dirURL?.openConnection() as JarURLConnection).jarFile
        }.onFailure {
            PluginUtils.plugin.logger.severe("Failed to extract directory from jar.")
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
                PluginUtils.plugin.logger.warning("$name already exists.")
                continue
            }

            if (entry.isDirectory) {
                if (!exists) {
                    runCatching {
                        Files.createDirectories(file)
                        PluginUtils.plugin.logger.info("Creating $name")
                    }.onFailure {
                        PluginUtils.plugin.logger.info("Failed to create $name")
                    }
                } else {
                    PluginUtils.plugin.logger.info("File named $name exists.")
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
                PluginUtils.plugin.logger.info("Writing $name")
            }.onFailure {
                PluginUtils.plugin.logger.info("Failed to write $name.")
                PluginUtils.plugin.logger.info("Can't extract $name from jar.")
            }
        }
    }
}