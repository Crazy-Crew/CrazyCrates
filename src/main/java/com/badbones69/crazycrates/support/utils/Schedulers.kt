package com.badbones69.crazycrates.support.utils

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class Schedulers(private val plugin: JavaPlugin) {

    /**
     * Better syntax for running sync tasks
     */
    fun queue(task: Runnable) = plugin.server.scheduler.runTask(plugin, task)

    /**
     * Better syntax for running task async
     */
    fun async(task: Runnable) = plugin.server.scheduler.runTaskAsynchronously(plugin, task)

    /**
     * Better syntax for running task later
     */
    fun later(delay: Long, task: Runnable) = plugin.server.scheduler.runTaskLater(plugin, task, delay)

    /**
     * Better syntax for running task later async
     */
    fun laterAsync(delay: Long, task: Runnable) = plugin.server.scheduler.runTaskLaterAsynchronously(plugin, task, delay)

    /**
     * Better syntax for running task timers
     */
    fun timer(period: Long, delay: Long = 0L, task: Runnable) = plugin.server.scheduler.runTaskTimer(plugin, task, delay, period)

    /**
     * Better syntax for running task timers async
     */
    fun asyncTimer(period: Long, delay: Long = 0L, task: Runnable) = plugin.server.scheduler.runTaskTimerAsynchronously(plugin, task, delay, period)
}