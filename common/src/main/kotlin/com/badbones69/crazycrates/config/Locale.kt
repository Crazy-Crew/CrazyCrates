package com.badbones69.crazycrates.config

import com.badbones69.crazycrates.files.AbstractConfig
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.nio.file.Path

object Locale : AbstractConfig() {

    fun reload(dataFolder: Path) = reload(dataFolder.resolve(Config().languageFile), this::class.java)

    fun send(recipient: Audience, msg: String, vararg placeholders: TagResolver.Single) = send(recipient, true, msg, *placeholders)

    fun send(recipient: Audience, prefix: Boolean, msg: String, vararg placeholders: TagResolver.Single) {
        msg.split("\n").forEach {
            send(recipient, prefix, parse(it, *placeholders))
        }
    }

    fun send(recipient: Audience, component: Component?) = send(recipient, true, component)

    fun send(recipient: Audience, prefix: Boolean, component: Component?) = recipient.sendMessage((if (prefix) parse("").append(component!!) else component)!!)

    private fun parse(msg: String?, vararg placeholders: TagResolver.Single): Component {
       return MiniMessage.miniMessage().deserialize(msg.toString(), *placeholders)
    }
}