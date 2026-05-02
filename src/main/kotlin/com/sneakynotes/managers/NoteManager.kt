package com.sneakynotes.managers

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

/**
 * Manages the lifecycle and metadata of notes created by the plugin.
 */
class NoteManager(private val plugin: JavaPlugin) {
    private val activeNotes = mutableSetOf<UUID>()

    /**
     * NamespacedKey for storing the creator's name in NBT.
     */
    val creatorKey = NamespacedKey(plugin, "creator_name")

    /**
     * NamespacedKey for marking an entity as a plugin-managed note.
     */
    val pluginKey = NamespacedKey(plugin, "is_sneaky_note")

    /**
     * Registers a note in memory and sets the NBT data.
     *
     * @param entity The TextDisplay entity to register
     * @param creatorName The name of the player who created the note
     */
    fun registerNote(
        entity: TextDisplay,
        creatorName: String,
    ) {
        val container = entity.persistentDataContainer
        container.set(pluginKey, PersistentDataType.BYTE, 1.toByte())
        container.set(creatorKey, PersistentDataType.STRING, creatorName)
        activeNotes.add(entity.uniqueId)
    }

    /**
     * Checks if a note is registered in the current session.
     *
     * @param uuid The UUID of the entity
     * @return true if the note is registered
     */
    fun isRegistered(uuid: UUID): Boolean = activeNotes.contains(uuid)

    /**
     * Checks if an entity is a plugin-managed note based on NBT data.
     *
     * @param entity The TextDisplay entity to check
     * @return true if it has the plugin's NBT key
     */
    fun isPluginNote(entity: TextDisplay): Boolean {
        return entity.persistentDataContainer.has(pluginKey, PersistentDataType.BYTE)
    }

    /**
     * Retrieves the creator's name from an entity's NBT data.
     *
     * @param entity The TextDisplay entity
     * @return The creator's name, or null if not found
     */
    fun getCreator(entity: TextDisplay): String? {
        return entity.persistentDataContainer.get(creatorKey, PersistentDataType.STRING)
    }

    /**
     * Removes a note from the active session tracking.
     *
     * @param uuid The UUID of the note to unregister
     */
    fun unregisterNote(uuid: UUID) {
        activeNotes.remove(uuid)
    }

    /**
     * Clears all active notes from session memory.
     */
    fun clear() {
        activeNotes.clear()
    }

    /**
     * Finds the nearest plugin-managed note to a player within a radius.
     *
     * @param player The player to search from
     * @param radius The search radius
     * @return The nearest TextDisplay entity, or null if none found
     */
    fun findNearestNote(
        player: Player,
        radius: Double,
    ): TextDisplay? {
        return player.getNearbyEntities(radius, radius, radius)
            .filterIsInstance<TextDisplay>()
            .filter { isPluginNote(it) }
            .minByOrNull { it.location.distanceSquared(player.location) }
    }
}
