package client;

import client.scenes.*;
import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.Module;

public class NotesModule implements Module {

    /**
     * Configures bindings for dependency injection.
     *
     * @param binder the binder used to bind dependencies as singletons.
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(MainNotesCtrl.class).in(Scopes.SINGLETON);
        binder.bind(NoteOverviewCtrl.class).in(Scopes.SINGLETON);
    }

}
