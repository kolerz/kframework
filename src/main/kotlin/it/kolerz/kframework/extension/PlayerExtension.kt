package it.kolerz.kframework.extensions

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Converts `&` color codes to `§` for use in Minecraft messages.
 *
 * Example:
 * ```kotlin
 * "&aHello &cWorld!".colorize() // "§aHello §cWorld!"
 * ```
 *
 * @return the string with color codes applied
 */
fun String.colorize(): String = ChatColor.translateAlternateColorCodes('&', this)

/**
 * Sends a colored message to the [CommandSender], converting `&` color codes automatically.
 *
 * Example:
 * ```kotlin
 * player.msg("&aWelcome to the server!")
 * ```
 *
 * @param message the message to send, supports `&` color codes
 */
fun CommandSender.msg(message: String) = sendMessage(message.colorize())

/**
 * Sends multiple colored messages to the [CommandSender].
 *
 * Example:
 * ```kotlin
 * player.msg(
 *     "&aLine one",
 *     "&bLine two",
 *     "&cLine three"
 * )
 * ```
 *
 * @param messages the messages to send, each supports `&` color codes
 */
fun CommandSender.msg(vararg messages: String) = messages.forEach { sendMessage(it.colorize()) }

/**
 * Ensures the [CommandSender] is a [Player] before executing the given action.
 * If the sender is not a player (e.g. console), sends an error message and returns false.
 *
 * Example:
 * ```kotlin
 * sender.requirePlayer {
 *     // this is Player here
 *     msg("&aYou are a player!")
 * }
 * ```
 *
 * @param action lambda with [Player] as receiver, executed only if sender is a player
 * @return true if the sender is a player, false otherwise
 */
fun CommandSender.requirePlayer(action: Player.() -> Unit): Boolean {
    if (this !is Player) {
        msg("&cOnly players can use this command!")
        return false
    }
    action(this)
    return true
}

/**
 * Broadcasts a colored message to all online players.
 *
 * Example:
 * ```kotlin
 * broadcastMsg("&eServer restart in 5 minutes!")
 * ```
 *
 * @param message the message to broadcast, supports `&` color codes
 */
fun broadcastMsg(message: String) {
    org.bukkit.Bukkit.getOnlinePlayers().forEach { it.msg(message) }
}