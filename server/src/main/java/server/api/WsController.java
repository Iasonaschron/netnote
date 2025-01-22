package server.api;

import commons.Note;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WsController {

    /**
     * Broadcasts the new note to all the clients that are subscribed to changes in notes
     * @param newNote the new note that is being broadcast
     * @return the new note with all its elements
     */
    @MessageMapping("/note-updates")
    @SendTo("/topic/note-updates")
    public Note broadcastNoteChanges(Note newNote){
        return newNote;
    }

    /**
     * Broadcasts the deletion of a note to all the clients that are subscribed to deletions in notes
     * @param deletedNote the note that is being deleted
     * @return the note that is being deleted
     */
    @MessageMapping("/note-deletions")
    @SendTo("/topic/note-deletions")
    public Note broadcastNoteDeletions(Note deletedNote){
        return deletedNote;
    }
}
