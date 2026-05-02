package com.sneakynotes.admincommands

import com.sneakynotes.SneakyNotes
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandNoteAdmin(private val plugin: SneakyNotes) : CommandBaseAdmin("noteadmin") {
    init {
        this.usageMessage = "/${this@CommandNoteAdmin.name} <who|remove|reload> [radius]"
        this.description = "Administrative commands for SneakyNotes."
    }

    override fun execute(
        sender: CommandSender,
        commandLabel: String,
        args: Array<out String>,
    ): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage(SneakyNotes.getMessage("usage-admin"))
            return true
        }

        when (args[0].lowercase()) {
            "reload" -> {
                plugin.reload()
                sender.sendMessage(SneakyNotes.getMessage("reload-success"))
            }
            "who" -> {
                if (sender !is Player) {
                    sender.sendMessage("Only players can use this.")
                    return true
                }
                val radius = args.getOrNull(1)?.toDoubleOrNull() ?: plugin.config.getDouble("remove-radius", 2.0)
                val nearest = plugin.noteManager.findNearestNote(sender, radius)
                if (nearest != null) {
                    val creator = plugin.noteManager.getCreator(nearest) ?: "Unknown"
                    sender.sendMessage(SneakyNotes.getMessage("nearest-creator", mapOf("%player%" to creator)))
                } else {
                    sender.sendMessage(SneakyNotes.getMessage("no-note-found"))
                }
            }
            "remove" -> {
                if (sender !is Player) {
                    sender.sendMessage("Only players can use this.")
                    return true
                }
                val radius = args.getOrNull(1)?.toDoubleOrNull() ?: plugin.config.getDouble("remove-radius", 2.0)
                val nearest = plugin.noteManager.findNearestNote(sender, radius)
                if (nearest != null) {
                    plugin.noteManager.unregisterNote(nearest.uniqueId)
                    nearest.remove()
                    sender.sendMessage(SneakyNotes.getMessage("note-removed"))
                } else {
                    sender.sendMessage(SneakyNotes.getMessage("no-note-found"))
                }
            }
            else -> sender.sendMessage(SneakyNotes.getMessage("usage-admin"))
        }

        return true
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
        sender: CommandSender,
        alias: String,
        args: Array<String>,
    ): List<String> {
        if (args.size == 1) {
            return listOf("who", "remove", "reload").filter { it.startsWith(args[0], true) }
        }
        return emptyList()
    }
}
