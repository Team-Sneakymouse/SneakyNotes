package com.sneakynotes.managers

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class NoteManager(private val plugin: JavaPlugin) {
    private val activeNotes = mutableSetOf<UUID>()
    val creatorKey = NamespacedKey(plugin, "creator_name")
    val pluginKey = NamespacedKey(plugin, "is_sneaky_note")

    fun registerNote(
        entity: TextDisplay,
        creatorName: String,
    ) {
        val container = entity.persistentDataContainer
        container.set(pluginKey, PersistentDataType.BYTE, 1.toByte())
        container.set(creatorKey, PersistentDataType.STRING, creatorName)
        activeNotes.add(entity.uniqueId)
    }

    fun isRegistered(uuid: UUID): Boolean = activeNotes.contains(uuid)

    fun isPluginNote(entity: TextDisplay): Boolean {
        return entity.persistentDataContainer.has(pluginKey, PersistentDataType.BYTE)
    }

    fun getCreator(entity: TextDisplay): String? {
        return entity.persistentDataContainer.get(creatorKey, PersistentDataType.STRING)
    }

    fun unregisterNote(uuid: UUID) {
        activeNotes.remove(uuid)
    }

    fun clear() {
        activeNotes.clear()
    }

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
