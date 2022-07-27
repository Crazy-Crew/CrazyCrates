package com.badbones69.crazycrates.config

import com.badbones69.crazycrates.files.files.AbstractConfig
import com.badbones69.crazycrates.utils.FileUtils
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

object Locale : AbstractConfig() {

    fun reload() {
        //reload(FileUtils().localeDir!!.resolve(Config.languageFile), this)
    }

    fun send(recipient: Audience, msg: String?, vararg placeholders: TagResolver.Single?) {
        send(recipient, true, msg, *placeholders)
    }

    fun send(recipient: Audience, prefix: Boolean, msg: String?, vararg placeholders: TagResolver.Single?) {
        //if (msg == null) return

        //for (part in msg.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
        //    send(recipient, prefix, parse(part, *placeholders))
        //}
    }

    fun send(recipient: Audience, prefix: Boolean, component: Component?) {
        //if (recipient is ConsoleCommandSender) {
       //     recipient.sendMessage((if (prefix) parse("bleh").append(component!!) else component)!!)
        //    return
        //}

        //recipient.sendMessage((if (prefix) parse("blah").append(component!!) else component)!!)
    }

    //fun parse(msg: String?, vararg placeholders: TagResolver.Single?): Component {
    //    return MiniMessage.miniMessage().deserialize(msg, *placeholders)
    //}
}