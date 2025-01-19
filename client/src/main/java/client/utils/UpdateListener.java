package client.utils;

import commons.Note;

public interface UpdateListener {
    public void handleNoteUpdate(Note updatedNote);
    public void handleNoteDeletion(Long deletedNoteId);
}