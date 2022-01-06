package me.badbones69.crazycrates.func.enums

import org.bukkit.entity.HumanEntity
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.PluginManager

enum class Permissions(private val node: String, val description: String, val permissionDefault: PermissionDefault) {

    CRATES_REMOVE_CRATE("crazycrates.remove.crate", "Remove a crate location", PermissionDefault.FALSE),
    CRATES_ADD_CRATE("crazycrates.add.crate", "Add a crate location", PermissionDefault.FALSE),

    CRATES_PREVIEW("crazycrates.preview", "Preview a crate", PermissionDefault.FALSE),
    CRATES_OPEN("crazycrates.open", "Opens a crate for a player", PermissionDefault.FALSE),
    CRATES_FORCE_OPEN("crazycrates.force.open", "Force opens a crate for a player", PermissionDefault.FALSE),
    CRATES_GIVE_KEY("crazycrates.give.key", "Gives a virtual/physical key to a player", PermissionDefault.FALSE),

    CRATES_TRANSFER("crazycrates.transfer", "Allow players to transfer keys between themselves", PermissionDefault.FALSE),
    CRATES_MENU("crazycrates.menu", "Opens the primary crate menu", PermissionDefault.FALSE),

    CRATES_ADMIN("crazycrates.admin", "Gives access to everything.", PermissionDefault.OP);

    fun getFullNode() = this.node
}

fun registerPermissions(pluginManager: PluginManager) {
    Permissions.values().forEach {
        if (pluginManager.getPermission(it.getFullNode()) == null)
            pluginManager.addPermission(
                Permission(
                    it.getFullNode(),
                    it.description,
                    it.permissionDefault
                )
            )
    }
}

fun getPermission(humanEntity: HumanEntity, permissions: Permissions) = humanEntity.hasPermission(permissions.getFullNode())