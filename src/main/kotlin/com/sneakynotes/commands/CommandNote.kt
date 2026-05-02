package com.sneakynotes.commands

import com.sneakynotes.SneakyNotes
import com.sneakynotes.util.TextUtility
import org.bukkit.Color
import org.bukkit.command.CommandSender
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Transformation
import org.joml.Vector3f

/**
 * Command for creating temporary floating notes.
 * Usage: /note [text]
 */
class CommandNote(private val plugin: SneakyNotes) : CommandBase("note") {
    init {
        this.usageMessage = "/${this@CommandNote.name} [text]"
        this.description = "Creates a temporary floating note."
    }

    /**
     * Executes the /note command.
     *
     * @param sender The sender of the command
     * @param commandLabel The label used to invoke the command
     * @param args The command arguments
     * @return true if the command was handled successfully
     */
    override fun execute(
        sender: CommandSender,
        commandLabel: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) {
            SneakyNotes.sendMessage(sender, TextUtility.convertToComponent("This command can only be used by players."))
            return true
        }

        if (args.isEmpty()) {
            SneakyNotes.sendMessage(sender, SneakyNotes.getMessage("usage-note"))
            return true
        }

        if (args[0] == "remove") {
            val radius = plugin.config.getDouble("remove-radius", 2.0)
            val nearest = plugin.noteManager.findNearestNote(sender, radius)
            if (nearest != null) {
                plugin.noteManager.unregisterNote(nearest.uniqueId)
                nearest.remove()
                SneakyNotes.sendMessage(sender, SneakyNotes.getMessage("note-removed"))
            } else {
                SneakyNotes.sendMessage(sender, SneakyNotes.getMessage("no-note-found"))
            }
            return true
        }

        val prefix = plugin.config.getString("note-settings.prefix", "") ?: ""
        var userInput = args.joinToString(" ")
        val limit = plugin.config.getInt("note-settings.char-limit", 100).coerceAtMost(100)

        if (userInput.length > limit) {
            userInput = userInput.substring(0, limit)
        }

        val text = prefix + userInput
        val player = sender

        val location = player.location
        val display =
            location.world.spawn(location, TextDisplay::class.java) { entity ->
                // Apply text
                entity.text(TextUtility.convertToComponent(text))

                // Apply settings from config
                val config = plugin.config

                val billboardStr = config.getString("note-settings.billboard", "CENTER") ?: "CENTER"
                entity.billboard = Billboard.valueOf(billboardStr.uppercase())

                val backgroundColor = config.getLong("note-settings.background-color", 0x40000000).toInt()
                entity.backgroundColor = Color.fromARGB(backgroundColor)

                entity.isShadowed = config.getBoolean("note-settings.shadow", true)
                entity.isSeeThrough = config.getBoolean("note-settings.see-through", false)

                val alignmentStr = config.getString("note-settings.alignment", "CENTER") ?: "CENTER"
                entity.alignment = TextDisplay.TextAlignment.valueOf(alignmentStr.uppercase())

                entity.textOpacity = config.getInt("note-settings.text-opacity", 255).toByte()
                entity.lineWidth = config.getInt("note-settings.line-width", 200)

                // Transformations
                val scaleX = config.getDouble("transformations.scale.x", 1.0).toFloat()
                val scaleY = config.getDouble("transformations.scale.y", 1.0).toFloat()
                val scaleZ = config.getDouble("transformations.scale.z", 1.0).toFloat()

                val transX = config.getDouble("transformations.translation.x", 0.0).toFloat()
                val transY = config.getDouble("transformations.translation.y", 0.5).toFloat()
                val transZ = config.getDouble("transformations.translation.z", 0.0).toFloat()

                val transformation =
                    Transformation(
                        Vector3f(transX, transY, transZ),
                        entity.transformation.leftRotation,
                        Vector3f(scaleX, scaleY, scaleZ),
                        entity.transformation.rightRotation,
                    )
                entity.transformation = transformation
            }

        plugin.noteManager.registerNote(display, player.name)
        SneakyNotes.sendMessage(sender, SneakyNotes.getMessage("note-created"))

        // Spy notification
        val spyMessage =
            SneakyNotes.getMessage(
                "note-spy",
                mapOf(
                    "%player%" to player.name,
                    "%text%" to userInput,
                ),
            )
        plugin.server.onlinePlayers
            .filter { it.hasPermission("sneakynotes.notespy") && it != player }
            .forEach { SneakyNotes.sendMessage(it, spyMessage) }

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
            return listOf("remove").filter { it.startsWith(args[0], true) }
        }
        return emptyList()
    }
}
