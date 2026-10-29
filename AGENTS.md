# Agent instructions

Follow `../EmptyPlugin/AGENTS.md` for the shared rules.

## Repo-specific rules

- Every note `TextDisplay` carries PDC keys `is_sneaky_note` and `creator_name`, set by `NoteManager.register`. Identify notes with `NoteManager.isPluginNote`, never by name or text.
- `ChunkListener` removes plugin notes that aren't registered in memory (leftovers from a previous session). Entities may not be loaded at `ChunkLoadEvent`; if notes survive, consider `EntitiesLoadEvent`. Don't change what `activeNotes`/registration means without updating it, or a reload deletes live notes.
- Display visuals come from config `note-settings.*` (read in `CommandNote`). Add new visual options as config keys with defaults, not hardcoded values; document them in the README.
- `creator_name` is shown by `/noteadmin who` and notespy. Keep that permission-gated and don't expose it through placeholders or to players.
