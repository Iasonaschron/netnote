package client;

import client.scenes.MainNotesCtrl;
import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.Module;

public class NotesModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(MainNotesCtrl.class).in(Scopes.SINGLETON);
    }

}
