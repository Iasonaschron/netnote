/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import commons.Note;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";

    /**
     * Fetches quotes from the server using raw HTTP connections. This method prints the quotes to the console.
     *
     * @throws IOException if an I/O error occurs during the connection or reading of data
     * @throws URISyntaxException if the URI is invalid
     */
    public void getQuotesTheHardWay() throws IOException, URISyntaxException {
        var url = new URI("http://localhost:8080/api/quotes").toURL();
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * Retrieves a list of quotes from the server.
     *
     * @return a list of {@link Quote} objects retrieved from the server
     */
    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {
                });
    }

    /**
     * Adds a new quote to the server.
     *
     * @param quote the {@link Quote} object to be added
     * @return the added {@link Quote} object with any updates from the server
     */
    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }

    /**
     * Checks if the server is running by attempting to make a request to it.
     *
     * @return true if the server is up, false if the server is unavailable
     */
    public boolean isServerAvailable() {
        try {
            ClientBuilder.newClient(new ClientConfig()) //
                    .target(SERVER) //
                    .request(APPLICATION_JSON) //
                    .get();
        } catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves a list of notes from the server.
     *
     * @return a list of {@link Note} objects retrieved from the server
     */
    public List<Note> getNotes() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/notes")
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Note>>() {
                });
    }

    /**
     * Adds a new note to the server.
     *
     * @param note the {@link Note} object to be added
     * @return the added {@link Note} object with any updates from the server
     */
    public Note addNote(Note note) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/notes")
                .request(APPLICATION_JSON)
                .post(Entity.entity(note, APPLICATION_JSON), Note.class);
    }

    /**
     * Sends a note to the server to be updated.
     *
     * @param id id of the note to be updated
     * @param note new version of the note to be updated in the database
     * @return note updated in the database
     */
    public boolean saveNote(long id, Note note) {
        try{
            ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER).path("api/notes/" + id)
                    .request(APPLICATION_JSON)
                    .put(Entity.entity(note, APPLICATION_JSON), Note.class);
            return true;
        }
        catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                return false;
            }
        }
        return false;
    }

    /**
     * Deletes a note from the server based on its ID.
     *
     * @param noteId the ID of the note to be deleted
     * @return true if the note was successfully deleted, false if there was an error
     */
    public boolean deleteNoteById(long noteId) {
        try {
            ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER).path("api/notes/" + noteId)
                    .request(APPLICATION_JSON)
                    .delete();
            return true;
        } catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                return false;
            }
        }
        return false;
    }
}