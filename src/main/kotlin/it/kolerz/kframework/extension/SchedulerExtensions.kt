package it.kolerz.kframework.extensions

import it.kolerz.kframework.KotlinFramework
import org.bukkit.scheduler.BukkitRunnable

/**
 * Runs a task after a given delay on the main server thread.
 *
 * Example:
 * ```kotlin
 * runLater(100L) {
 *     player.msg("&a5 seconds have passed!")
 * }
 * ```
 *
 * @param delay the delay in ticks before the task runs (20 ticks = 1 second)
 * @param action the lambda to execute after the delay
 */
fun runLater(delay: Long, action: () -> Unit) {
    object : BukkitRunnable() {
        override fun run() {
            try {
                action()
            } catch (e: Exception) {
                KotlinFramework.instance.logger.severe("Errore in runLater: ${e.message}")
            }
        }
    }.runTaskLater(KotlinFramework.instance, delay)
}

/**
 * Runs a repeating task on the main server thread.
 * Returns the [BukkitRunnable] so it can be cancelled later.
 *
 * Example:
 * ```kotlin
 * val task = runTimer(0L, 20L) {
 *     broadcastMsg("&eThis runs every second!")
 * }
 * // later...
 * task.cancel()
 * ```
 *
 * @param delay the delay in ticks before the first execution (20 ticks = 1 second)
 * @param period the interval in ticks between executions (20 ticks = 1 second)
 * @param action the lambda to execute on each tick, with [BukkitRunnable] as receiver (can call [BukkitRunnable.cancel] to stop)
 * @return the [BukkitRunnable] instance, can be used to cancel the task
 */
fun runTimer(delay: Long, period: Long, action: BukkitRunnable.() -> Unit): BukkitRunnable {
    val task = object : BukkitRunnable() {
        override fun run() {
            try {
                action()
            } catch (e: Exception) {
                KotlinFramework.instance.logger.severe("Errore in runTimer: ${e.message}")
                cancel()
            }
        }
    }
    task.runTaskTimer(KotlinFramework.instance, delay, period)
    return task
}

/**
 * Runs a task asynchronously off the main server thread.
 * Use this for heavy operations like database queries or HTTP requests.
 *
 * **Warning:** do not interact with the Bukkit API inside this block,
 * as most API calls are not thread-safe. Use [runLater] to come back to the main thread.
 *
 * Example:
 * ```kotlin
 * runAsync {
 *     val data = database.fetchPlayerData(player.uniqueId)
 *     runLater(0L) {
 *         player.msg("&aData loaded: $data")
 *     }
 * }
 * ```
 *
 * @param action the lambda to execute asynchronously
 */
fun runAsync(action: () -> Unit) {
    object : BukkitRunnable() {
        override fun run() {
            try {
                action()
            } catch (e: Exception) {
                KotlinFramework.instance.logger.severe("Errore in runAsync: ${e.message}")
            }
        }
    }.runTaskAsynchronously(KotlinFramework.instance)
}