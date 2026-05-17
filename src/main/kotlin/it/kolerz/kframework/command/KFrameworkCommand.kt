package it.kolerz.kframework.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class KFrameworkCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty()) {
            sender.sendMessage("§8§m                                        ")
            sender.sendMessage("  §cKotlin§fFramework §7v1.0.0")
            sender.sendMessage("  §7Lightweight Kotlin framework for Paper.")
            sender.sendMessage("  §7Author: §fkolerz")
            sender.sendMessage("  §7GitHub: §fgithub.com/kolerz/kframework")
            sender.sendMessage("§8§m                                        ")
            return true
        }

        when (args[0].lowercase()) {
            "version" -> sender.sendMessage("§7KotlinFramework §fv1.0.0 §7by §fkolerz")
            "help" -> {
                sender.sendMessage("§cKotlinFramework §7commands:")
                sender.sendMessage("§7/kframework §8- §fShow info")
                sender.sendMessage("§7/kframework version §8- §fShow version")
                sender.sendMessage("§7/kframework help §8- §fShow this help")
            }
            else -> sender.sendMessage("§cUnknown subcommand. Use §f/kframework help")
        }

        return true
    }
}