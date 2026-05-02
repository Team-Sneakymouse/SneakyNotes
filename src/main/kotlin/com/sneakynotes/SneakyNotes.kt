package com.sneakynotes

import com.sneakynotes.admincommands.CommandNoteAdmin
import com.sneakynotes.commands.CommandNote
import com.sneakynotes.listeners.ChunkListener
import com.sneakynotes.managers.NoteManager
import com.sneakynotes.util.TextUtility
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.java.JavaPlugin

class SneakyNotes : JavaPlugin() {
    /**
     * Manager responsible for entity lifecycle and NBT data.
     */
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

        // Register permission nodes
        server.pluginManager.addPermission(Permission("sneakynotes.command.*", "Allows use of all /note subcommands", PermissionDefault.OP))
        server.pluginManager.addPermission(Permission("sneakynotes.admincommand.*", "Allows use of all /noteadmin subcommands", PermissionDefault.OP))
        server.pluginManager.addPermission(Permission("sneakynotes.notespy", "Receives notifications when players create notes", PermissionDefault.OP))

        logger.info("SneakyNotes has been enabled!")
    }

    /**
     * Performs cleanup on plugin disable.
     */
    override fun onDisable() {
        noteManager.clear()
        logger.info("SneakyNotes has been disabled!")
    }

    /**
     * Reloads the plugin configuration from disk.
     */
    fun reload() {
        reloadConfig()
        // No need to clear activeNotes on reload, they are still the same session
    }

    companion object {
        private lateinit var instance: SneakyNotes

        /**
         * The unique identifier for the plugin (namespace).
         */
        const val IDENTIFIER = "sneakynotes"

        /**
         * Gets the current plugin instance.
         *
         * @return The SneakyNotes instance
         */
        fun getInstance(): SneakyNotes = instance

        /**
         * Retrieves a formatted message from the configuration.
         *
         * @param key The message key in config.yml
         * @return The formatted Component
         */
        fun getMessage(key: String): Component {
            return getMessage(key, emptyMap())
        }

        /**
         * Retrieves a formatted message from the configuration with placeholders.
         *
         * @param key The message key in config.yml
         * @param placeholders A map of keys to replace with values
         * @return The formatted Component
         */
        fun getMessage(
            key: String,
            placeholders: Map<String, String>,
        ): Component {
            val prefix = instance.config.getString("messages.prefix", "")
            var message = instance.config.getString("messages.$key", "Missing message: $key") ?: "Missing message: $key"

            placeholders.forEach { (key, value) ->
                message = message.replace(key, value)
            }

            return TextUtility.convertToComponent((prefix + message))
        }

        /**
         * Sends a message to a CommandSender if it's not empty.
         *
         * @param sender The sender to receive the message
         * @param component The message component
         */
        fun sendMessage(
            sender: CommandSender,
            component: Component,
        ) {
            val plainText =
                net.kyori.adventure.text.serializer.plain.PlainComponentSerializer
                    .plain()
                    .serialize(component)
            if (plainText.trim().isNotEmpty()) {
                sender.sendMessage(component)
            }
        }
    }
}
