package it.kolerz.kframework.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

/**
 * Base class for commands with a clean syntax, without boilerplate.
 * Extend this instead of [CommandExecutor] for a more Kotlin-friendly experience.
 *
 * Example:
 * ```kotlin
 * class SpawnCommand : KotlinCommand(playerOnly = true) {
 *     override fun CommandContext.execute() {
 *         SpawnManager.teleportToSpawn(player!!)
 *         reply("&aTeleported to spawn!")
 *     }
 * }
 *
 * // with permission:
 * class KickCommand : KotlinCommand(playerOnly = false, permission = "myplugin.kick") {
 *     override fun CommandContext.execute() {
 *         requireArgs(1, "/kick <player>") {
 *             val target = Bukkit.getPlayer(args[0])
 *             if (target == null) {
 *                 reply("&cPlayer not found!")
 *                 return@requireArgs
 *             }
 *             target.kickPlayer("Kicked!")
 *             reply("&aKicked ${target.name}!")
 *         }
 *     }
 * }
 * ```
 *
 * @property playerOnly if true, only players can execute this command. Defaults to false
 * @property permission optional permission node required to execute the command
 * @property permissionMessage the message sent when the sender lacks the required permission
 */
abstract class KotlinCommand(
    val playerOnly: Boolean = false,
    val permission: String? = null,
    val permissionMessage: String = "&cYou don't have permission to use this command!"
) : CommandExecutor {

    /**
     * The command logic. Implement this in your subclass.
     * Runs with [CommandContext] as receiver, giving access to [CommandContext.sender],
     * [CommandContext.player], [CommandContext.args], and [CommandContext.reply].
     */
    abstract fun CommandContext.execute()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (playerOnly && sender !is org.bukkit.entity.Player) {
            sender.sendMessage("§cOnly players can use this command!")
            return true
        }

        if (permission != null && !sender.hasPermission(permission!!)) {
            sender.sendMessage(permissionMessage.replace("&", "§"))
            return true
        }

        val context = CommandContext(sender, args, sender as? org.bukkit.entity.Player)
        context.execute()
        return true
    }
}