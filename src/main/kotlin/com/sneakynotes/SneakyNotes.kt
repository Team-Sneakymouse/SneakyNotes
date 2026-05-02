package com.sneakynotes

import com.sneakynotes.commands.*
import com.sneakynotes.admincommands.*
import com.sneakynotes.listeners.ChunkListener
import com.sneakynotes.util.NoteManager
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.plugin.java.JavaPlugin

class SneakyNotes : JavaPlugin() {
    lateinit var noteManager: NoteManager
    val miniMessage = MiniMessage.miniMessage()

    override fun onEnable() {
        saveDefaultConfig()
        noteManager = NoteManager(this)

        // Register commands
        server.commandMap.register(IDENTIFIER, CommandNote(this))
        server.commandMap.register(IDENTIFIER, CommandNoteAdmin(this))

        // Register listeners
        server.pluginManager.registerEvents(ChunkListener(this), this)

        logger.info("SneakyNotes has been enabled!")
    }

    override fun onDisable() {
        noteManager.clear()
        logger.info("SneakyNotes has been disabled!")
    }

    fun reload() {
        reloadConfig()
        // No need to clear activeNotes on reload, they are still the same session
    }

    fun getMessage(key: String, vararg placeholders: net.kyori.adventure.text.minimessage.tag.resolver.TagResolver): net.kyori.adventure.text.Component {
        val prefix = config.getString("messages.prefix", "")
        val message = config.getString("messages.$key", "Missing message: $key")
        return miniMessage.deserialize("$prefix$message", *placeholders)
    }

	companion object {
		const val IDENTIFIER = "sneakynotes"
	}
}
