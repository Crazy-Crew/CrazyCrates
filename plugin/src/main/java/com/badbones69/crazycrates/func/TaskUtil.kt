package com.badbones69.crazycrates.func

import com.badbones69.crazycrates.getPlugin

object TaskUtil {

    /**
     * Better syntax for running sync tasks
     */
    fun queue(task: Runnable) = getPlugin().server.scheduler.runTask(getPlugin(), task)

    /**
     * Better syntax for running task async
     */
    fun async(task: Runnable) = getPlugin().server.scheduler.runTaskAsynchronously(getPlugin(), task)

    /**
     * Better syntax for running task later
     */
    fun later(delay: Long, task: Runnable) = getPlugin().server.scheduler.runTaskLater(getPlugin(), task, delay)

    /**
     * Better syntax for running task later async
     */
    fun laterAsync(delay: Long, task: Runnable) = getPlugin().server.scheduler.runTaskLaterAsynchronously(getPlugin(), task, delay)

    /**
     * Better syntax for running task timers
     */
    fun timer(period: Long, delay: Long = 0L, task: Runnable) = getPlugin().server.scheduler.runTaskTimer(getPlugin(), task, delay, period)

    /**
     * Better syntax for running task timers async
     */
    fun asyncTimer(period: Long, delay: Long = 0L, task: Runnable) = getPlugin().server.scheduler.runTaskTimerAsynchronously(getPlugin(), task, delay, period)
}