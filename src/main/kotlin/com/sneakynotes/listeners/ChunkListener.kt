package com.sneakynotes.listeners

import com.sneakynotes.SneakyNotes
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent

/**
 * Listener responsible for cleaning up notes from previous sessions when chunks load.
 */
class ChunkListener(private val plugin: SneakyNotes) : Listener {
    /**
     * Cleans up orphaned notes in the loading chunk.
     *
     * @param event The ChunkLoadEvent
     */
    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        // Check all entities in the chunk
        for (entity in event.chunk.entities) {
            if (entity is TextDisplay) {
                // If it's our plugin's note but NOT in our memory, it's from a previous session
                if (plugin.noteManager.isPluginNote(entity)) {
                    if (!plugin.noteManager.isRegistered(entity.uniqueId)) {
                        entity.remove()
                    }
                }
            }
        }
    }
}
