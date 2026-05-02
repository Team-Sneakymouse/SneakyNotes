package com.sneakynotes

import com.sneakynotes.commands.*
import com.sneakynotes.admincommands.*
import com.sneakynotes.listeners.ChunkListener
import com.sneakynotes.managers.NoteManager
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin
import com.sneakynotes.util.TextUtility

class SneakyNotes : JavaPlugin() {
    lateinit var noteManager: NoteManager

    /**
     * Initializes the plugin instance during server load.
     */
    override fun onLoad() {
        instance = this
    }

	/**
     * Performs plugin setup on enable:
     * - Initializes managers
     * - Registers commands and listeners
     */
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

	companion object {
        private lateinit var instance: SneakyNotes
		const val IDENTIFIER = "sneakynotes"
		
		fun getInstance(): SneakyNotes = instance

		fun getMessage(key: String): Component {
			return getMessage(key, emptyMap())
		}

		fun getMessage(key: String, placeholders: Map<String, String>): Component {
			val prefix = instance.config.getString("messages.prefix", "")
			var message = instance.config.getString("messages.$key", "Missing message: $key") ?: "Missing message: $key"
			
			placeholders.forEach { (key, value) ->
				message = message.replace(key, value)
			}
			
			return TextUtility.convertToComponent((prefix + message))
		}
	}
}
