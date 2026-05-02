# SneakyNotes

A Minecraft Paper plugin for creating temporary floating notes using Text Display entities.

## Features
- Create notes with `/note [text]`
- Configurable display settings (billboard, background, transformations, etc.)
- Notes persist for the current session only (cleaned up on chunk load if from a previous session)
- Admin tools to identify note creators and remove nearby notes
- Fully configurable messages and settings

## Commands
- `/note [text]` - Create a note at your current location.
- `/noteadmin who` - Show the creator of the nearest note.
- `/noteadmin remove [radius]` - Remove the nearest note within the specified radius (defaults to config value).
- `/noteadmin reload` - Reload the plugin configuration.

## Permissions
- `sneakynotes.command.note` - Allows use of `/note` (default: op)
- `sneakynotes.admincommand.noteadmin` - Allows use of `/noteadmin` (default: op)
- `sneakynotes.notespy` - Receive notifications when players create notes (default: op)

## Configuration
The `config.yml` allows you to customize:
- `note-settings`: Billboard type, background color, shadow, see-through, alignment, and opacity.
- `transformations`: Default scale and translation for the notes.
- `remove-radius`: Default radius for the remove command.
- `messages`: All plugin messages with MiniMessage (Adventure) support.
