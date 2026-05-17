package it.kolerz.kframework.command

import it.kolerz.kframework.extensions.msg
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Internal command executor used by [it.kolerz.kframework.KotlinFramework.registerCommand]
 * when registering commands inline.
 * You don't need to use this directly — use `registerCommand { }` in your plugin instead.
 *
 * @property playerOnly if true, only players can execute this command
 * @property permission optional permission node required to execute the command
 * @property permissionMessage the message sent when the sender lacks the required permission
 * @property usage optional usage message shown when arguments are missing
 * @property executor the command logic as a lambda with [CommandContext] as receiver
 */
class CommandBuilder(
    private val playerOnly: Boolean = false,
    private val permission: String? = null,
    private val permissionMessage: String = "&cYou don't have permission to use this command!",
    private val usage: String? = null,
    private val executor: CommandContext.() -> Unit
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (playerOnly && sender !is Player) {
            sender.msg("&cOnly players can use this command!")
            return true
        }

        if (permission != null && !sender.hasPermission(permission)) {
            sender.msg(permissionMessage)
            return true
        }

        val context = CommandContext(sender, args, sender as? Player)
        executor(context)
        return true
    }
}

/**
 * Contains all the context available inside an inline command or [KotlinCommand.execute].
 * Provides access to the sender, player, arguments, and utility methods.
 *
 * @property sender the [CommandSender] who executed the command
 * @property args the array of arguments passed to the command
 * @property player the [Player] who executed the command, or null if executed from console
 */
class CommandContext(
    val sender: CommandSender,
    val args: Array<out String>,
    val player: Player?
) {

    /**
     * Sends a colored message to the [sender], converting `&` color codes automatically.
     *
     * Example:
     * ```kotlin
     * reply("&aCommand executed successfully!")
     * ```
     *
     * @param message the message to send, supports `&` color codes
     */
    fun reply(message: String) = sender.msg(message)

    /**
     * Checks that the command has at least [min] arguments.
     * If not, sends the usage message and returns without executing [action].
     *
     * Example:
     * ```kotlin
     * requireArgs(2, "/msg <player> <message>") {
     *     // guaranteed to have at least 2 args here
     *     val target = Bukkit.getPlayer(args[0])
     *     val message = args.drop(1).joinToString(" ")
     * }
     * ```
     *
     * @param min the minimum number of arguments required
     * @param usage the usage string shown if arguments are insufficient
     * @param action the lambda to execute if the argument check passes
     */
    fun requireArgs(min: Int, usage: String, action: () -> Unit) {
        if (args.size < min) {
            reply("&cUsage: $usage")
            return
        }
        action()
    }
}