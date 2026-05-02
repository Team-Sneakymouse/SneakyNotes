package com.sneakynotes.commands

import com.sneakynotes.SneakyNotes
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Color
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.entity.Display.Billboard
import org.bukkit.util.Transformation
import org.joml.Vector3f

class CommandNote(private val plugin: SneakyNotes) : CommandBase("note") {

	init {
		this.usageMessage = "/${this@CommandNote.name} [text]"
		this.description = "Creates a temporary floating note."
	}

    override fun execute(
        sender: CommandSender, commandLabel: String, args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be used by players.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(plugin.getMessage("usage-note"))
            return true
        }

        val text = args.joinToString(" ")
        val player = sender

        val location = player.location
        val display = location.world.spawn(location, TextDisplay::class.java) { entity ->
            // Apply text
            entity.text(plugin.miniMessage.deserialize(text))

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

            // Transformations
            val scaleX = config.getDouble("transformations.scale.x", 1.0).toFloat()
            val scaleY = config.getDouble("transformations.scale.y", 1.0).toFloat()
            val scaleZ = config.getDouble("transformations.scale.z", 1.0).toFloat()
            
            val transX = config.getDouble("transformations.translation.x", 0.0).toFloat()
            val transY = config.getDouble("transformations.translation.y", 0.5).toFloat()
            val transZ = config.getDouble("transformations.translation.z", 0.0).toFloat()

            val transformation = Transformation(
                Vector3f(transX, transY, transZ),
                entity.transformation.leftRotation,
                Vector3f(scaleX, scaleY, scaleZ),
                entity.transformation.rightRotation
            )
            entity.transformation = transformation
        }

        plugin.noteManager.registerNote(display, player.name)
        sender.sendMessage(plugin.getMessage("note-created"))

        return true
    }
}
