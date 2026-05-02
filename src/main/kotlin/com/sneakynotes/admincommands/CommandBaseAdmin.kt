package com.sneakynotes.admincommands

import com.sneakynotes.SneakyNotes
import org.bukkit.command.Command

/**
 * Base class for all plugin commands that are specific to administrators.
 * Provides common setup and permission handling.
 *
 * @property name The name of the command
 */
abstract class CommandBaseAdmin(name: String) : Command(name) {

    init {
        this.permission = "${SneakyNotes.IDENTIFIER}.commandadmin.$name"
    }

}