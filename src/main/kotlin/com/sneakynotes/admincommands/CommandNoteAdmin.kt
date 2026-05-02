package com.sneakynotes.admincommands

import com.sneakynotes.SneakyNotes
import com.sneakynotes.admincommands.CommandBaseAdmin
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay

class CommandNoteAdmin(private val plugin: SneakyNotes) : CommandBaseAdmin("noteadmin") {

    init {
        this.usageMessage = "/${this@CommandNoteAdmin.name} <who|remove|reload> [radius]"
        this.description = "Administrative commands for SneakyNotes."
    }

    override fun execute(
        sender: CommandSender, commandLabel: String, args: Array<out String>
    ): Boolean {
		if (args.isEmpty()) {
            sender.sendMessage(plugin.getMessage("usage-admin"))
            return true
        }

        when (args[0].lowercase()) {
            "reload" -> {
                plugin.reload()
                sender.sendMessage(plugin.getMessage("reload-success"))
            }
            "who" -> {
                if (sender !is Player) {
                    sender.sendMessage("Only players can use this.")
                    return true
                }
                val nearest = findNearestNote(sender)
                if (nearest != null) {
                    val creator = plugin.noteManager.getCreator(nearest) ?: "Unknown"
                    sender.sendMessage(plugin.getMessage("nearest-creator", Placeholder.unparsed("player", creator)))
                } else {
                    sender.sendMessage(plugin.getMessage("no-note-found"))
                }
            }
            "remove" -> {
                if (sender !is Player) {
                    sender.sendMessage("Only players can use this.")
                    return true
                }
                val radius = args.getOrNull(1)?.toDoubleOrNull() ?: plugin.config.getDouble("remove-radius", 5.0)
                val nearest = findNearestNote(sender, radius)
                if (nearest != null) {
                    plugin.noteManager.unregisterNote(nearest.uniqueId)
                    nearest.remove()
                    sender.sendMessage(plugin.getMessage("note-removed"))
                } else {
                    sender.sendMessage(plugin.getMessage("no-note-found"))
                }
            }
            else -> sender.sendMessage(plugin.getMessage("usage-admin"))
        }

        return true
    }

    private fun findNearestNote(player: Player, radius: Double = plugin.config.getDouble("remove-radius", 5.0)): TextDisplay? {
        return player.getNearbyEntities(radius, radius, radius)
            .filterIsInstance<TextDisplay>()
            .filter { plugin.noteManager.isPluginNote(it) }
            .minByOrNull { it.location.distanceSquared(player.location) }
    }

    /**
     * Provides tab completion for the command arguments.
     *
     * @param sender The entity that sent the command.
     * @param alias The alias used to invoke the command.
     * @param args The arguments provided with the command.
     * @return A list of possible completions based on the current input.
     */
    override fun tabComplete(
        sender: CommandSender, alias: String, args: Array<String>
    ): List<String> {
        if (args.size == 1) {
            return listOf("who", "remove", "reload").filter { it.startsWith(args[0], true) }
        }
        return emptyList()
    }
}
