package it.kolerz.kframework

import it.kolerz.kframework.command.CommandBuilder
import it.kolerz.kframework.command.CommandContext
import it.kolerz.kframework.command.KFrameworkCommand
import it.kolerz.kframework.listener.KotlinListener
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

/**
 * Base class for plugins using KotlinFramework.
 * Extend this instead of [JavaPlugin] to get access to all framework utilities.
 *
 * Example usage:
 * ```kotlin
 * class MyPlugin : KotlinFramework() {
 *     override fun onStart() {
 *         registerCommand("spawn", playerOnly = true) {
 *             SpawnManager.teleportToSpawn(player!!)
 *         }
 *     }
 * }
 * ```
 */
abstract class KotlinFramework : JavaPlugin() {

    companion object {
        /**
         * Global instance of the plugin extending [KotlinFramework].
         * Available after [onEnable] is called.
         */
        lateinit var instance: KotlinFramework
            private set
    }

    override fun onEnable() {
        instance = this
        logger.info("╔══════════════════════════════════════╗")
        logger.info("║         KotlinFramework v1.0.0       ║")
        logger.info("║                                      ║")
        logger.info("║  Lightweight Kotlin framework for    ║")
        logger.info("║  Paper plugin development.           ║")
        logger.info("║                                      ║")
        logger.info("║  Author: Kolerz_                      ║")
        logger.info("║  GitHub: github.com/kolerz/kframework║")
        logger.info("╚══════════════════════════════════════╝")
        logger.info("╔══════════════════════════════╗")
        logger.info("║  ${description.name} v${description.version}")
        logger.info("║  by ${description.authors.joinToString(", ")}")
        logger.info("║  Powered by KotlinFramework")
        logger.info("╚══════════════════════════════╝")
        getCommand("kframework")?.setExecutor(KFrameworkCommand())
        try {
            onStart()
            logger.info("[${description.name}] plugin enabled")
        } catch (e: Exception) {
            logger.severe("[${description.name}] Error: ${e.message}")
            server.pluginManager.disablePlugin(this)
        }
    }

    override fun onDisable() {
        try {
            onStop()
        } catch (e: Exception) {
            logger.severe("[${description.name}] Error: ${e.message}")
        }
        logger.info("[${description.name}] plugin disabled")
    }

    /**
     * Called when the plugin is enabled.
     * Override this instead of [onEnable] to register commands and listeners.
     */
    open fun onStart() {}

    /**
     * Called when the plugin is disabled.
     * Override this instead of [onDisable] for cleanup logic.
     */
    open fun onStop() {}

    /**
     * Registers a command with a [CommandExecutor] class.
     * The command must be declared in plugin.yml.
     *
     * @param name the command name as declared in plugin.yml
     * @param executor the [CommandExecutor] handling the command
     */
    fun registerCommand(name: String, executor: CommandExecutor) {
        val cmd = getCommand(name)
        if (cmd == null) {
            logger.warning("Command '$name' not found in plugin.yml")
            return
        }
        cmd.setExecutor(executor)
    }

    /**
     * Registers a command inline without a separate class.
     * The command must be declared in plugin.yml.
     *
     * @param name the command name as declared in plugin.yml
     * @param playerOnly if true, only players can execute this command
     * @param permission optional permission node required to execute the command
     * @param executor lambda with [CommandContext] as receiver
     */
    fun registerCommand(name: String, playerOnly: Boolean = false, permission: String? = null, executor: CommandContext.() -> Unit) {
        val cmd = getCommand(name)
        if (cmd == null) {
            logger.warning("Command '$name' not found in plugin.yml")
            return
        }
        cmd.setExecutor(CommandBuilder(playerOnly, permission, executor = executor))
    }

    /**
     * Registers multiple commands at once.
     *
     * @param commands pairs of command name to [CommandExecutor]
     */
    fun registerCommands(vararg commands: Pair<String, CommandExecutor>) {
        commands.forEach { (name, executor) -> registerCommand(name, executor) }
    }

    /**
     * Registers a [Listener] class.
     *
     * @param listener the listener to register
     */
    fun registerListener(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    /**
     * Registers multiple [Listener] classes at once.
     *
     * @param listeners the listeners to register
     */
    fun registerListeners(vararg listeners: Listener) {
        listeners.forEach { registerListener(it) }
    }

    /**
     * Registers events inline without a separate Listener class.
     *
     * Example:
     * ```kotlin
     * registerListener {
     *     on<PlayerJoinEvent> {
     *         player.msg("&aWelcome!")
     *     }
     * }
     * ```
     *
     * @param block lambda with [KotlinListener] as receiver
     */
    fun registerListener(block: KotlinListener.() -> Unit) {
        val l = KotlinListener()
        l.block()
    }
}