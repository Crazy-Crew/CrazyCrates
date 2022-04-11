package com.badbones69.crazycrates.v2.utils.types.json.persist

import com.badbones69.crazycrates.api.FileManager.Files
import com.badbones69.crazycrates.v2.utils.types.json.JsonManager

object Test {

    var testString = "Hey"

    fun save() = JsonManager.save(Files.TEST.fileName, this)

    fun load() = JsonManager.load(Files.TEST.fileName, Test::class.java, this)
}