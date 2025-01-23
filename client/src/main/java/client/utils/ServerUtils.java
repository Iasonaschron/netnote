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

import java.io.File;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;

import commons.FileData;
import commons.Note;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

public class ServerUtils {

    private static final String DEFAULT_SERVER = "http://localhost:8080/";

    private static final String SERVER = "http://localhost:8080/";

    /**
     * Checks if the server is running by attempting to make a request to it.
     *
     * @param server The server targeted
     * @return true if the server is up, false if the server is unavailable
     */
    public boolean isServerAvailable(String server) {
        try {
            ClientBuilder.newClient(new ClientConfig()) //
                    .target(server) //
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
     * @param server The server targeted
     * @return a list of {@link Note} objects retrieved from the server
     */
    public List<Note> getNotes(String server) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/notes")
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Note>>() {
                });
    }

    /**
     * Adds a new note to the server.
     *
     * @param server The server targeted
     * @param note the {@link Note} object to be added
     * @return the added {@link Note} object with any updates from the server
     */
    public Note addNote(Note note, String server) throws ProcessingException {
        try{
            return ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("api/notes")
                    .request(APPLICATION_JSON)
                    .post(Entity.entity(note, APPLICATION_JSON), Note.class);
        }catch(ProcessingException e){
            throw new ProcessingException("The server" + server + " is not available");
        }
    }

    /**
     * Performs a request to the server
     *
     * @param noteid The id of the note
     * @return the names of the files related with noteid
     */
    public List<FileData> fetchFileNames(long noteid) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/files/" + noteid)
                .request(APPLICATION_JSON)
                .get(new GenericType<List<FileData>>() {
                });
    }

    /**
     *
     * @param noteid the note id
     * @param filename the filename
     * @return an input stream containing the binary data of the file to be downloaded
     */
    public InputStream downloadFile(long noteid, String filename){
        Client client = ClientBuilder.newClient(new ClientConfig());
        Response response = client.target(SERVER).path("api/files/" + noteid + "/" + filename + "/download")
                .request(MediaType.APPLICATION_OCTET_STREAM)
                .get();
        return response.readEntity(InputStream.class);
    }

    /**
     * Makes a request to the server to change the name of a specific file
     * @param noteid the id of the note
     * @param oldName the name to be changed
     * @param newName the new name
     * @return if the operation was successful
     */
    public boolean changeFileName(long noteid, String oldName, String newName){
        try{
            ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER).path("api/files/" + noteid + "/" + oldName + "/change")
                    .request(APPLICATION_JSON)
                    .post(Entity.entity(newName, APPLICATION_JSON), String.class);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Makes a request to the server to delete all files
     * @return if the operation was successful
     */
    public boolean deleteAllFiles(){
        try{
            ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER).path("api/files/all")
                    .request(APPLICATION_JSON)
                    .delete();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Makes a request to the server to delete a specific file, or all files in a note, if filename is null
     * @param noteid
     * @param filename
     * @return if the operation was successful
     */
    public boolean deleteFile(long noteid, String filename){
        try{
            if(filename == null){
                ClientBuilder.newClient(new ClientConfig())
                        .target(SERVER).path("api/files/" + noteid + "/all")
                        .request(APPLICATION_JSON)
                        .delete();
                return true;
            }
            ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER).path("api/files/" + noteid + "/" + filename)
                    .request(APPLICATION_JSON)
                    .delete();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param file the file to be uploaded
     * @param noteid the id of the note
     * @return either if the operation was successful or not
     */
    public boolean uploadFile(File file, long noteid) {
        try{
            Client client = ClientBuilder.newClient(new ClientConfig());

            FormDataMultiPart form = new FormDataMultiPart();
            form.field("filename", file.getName());
            form.bodyPart(new FileDataBodyPart("file", file, MediaType.APPLICATION_OCTET_STREAM_TYPE));

            Response response = client
                    .target(SERVER).path("api/files/" + noteid + "/upload")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));

            form.close();
            client.close();

            if (response.getStatus() == 200) {
                System.out.println("File uploaded successfully: " + response.readEntity(String.class));
                return true;
            } else {
                System.err.println("Failed to upload file: " + response.getStatus());
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Sends a note to the server to be updated.
     *
     * @param id   id of the note to be updated
     * @param note new version of the note to be updated in the database
     * @param server The server targeted
     * @return note updated in the database
     */
    public boolean saveNote(long id, Note note, String server) {
        try {
            ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("api/notes/" + id)
                    .request(APPLICATION_JSON)
                    .put(Entity.entity(note, APPLICATION_JSON), Note.class);
            return true;
        } catch (ProcessingException e) {
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
     * @param server The server targeted
     * @return true if the note was successfully deleted, false if there was an error
     */
    public boolean deleteNoteById(long noteId, String server) {
        try {
            ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("api/notes/" + noteId)
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

    /**
     * Retrieves a note from the server by its ID.
     *
     * @param id the ID of the note to be retrieved
     * @param server The server targeted
     * @return the {@link Note} object retrieved from the server, or null if not found
     */
    public Note getNoteById(long id, String server) {
        try {
            return ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("api/notes/" + id)
                    .request(APPLICATION_JSON)
                    .get(Note.class);
        } catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                return null;
            }
        }
        return null;
    }
}