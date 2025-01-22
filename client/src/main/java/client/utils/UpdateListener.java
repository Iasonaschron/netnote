package client.utils;

import commons.Note;

public interface UpdateListener {
    /**
     * Handles the updates on notes.
     * @param updatedNote the updated note
     */
    public void handleNoteUpdate(Note updatedNote);

    /**
     * Handles the deletion of a note.
     * @param deletedNoteId the id of the deleted note
     */
    public void handleNoteDeletion(Long deletedNoteId);
}