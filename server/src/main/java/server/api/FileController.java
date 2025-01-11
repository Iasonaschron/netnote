package server.api;

import commons.FileCompositeKey;
import commons.FileData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.database.FileRepository;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileRepository repo;

    /**
     * Initializes the FileController with a file repo
     * @param repo the repository that holds the files
     */
    public FileController(FileRepository repo) {
        this.repo = repo;
    }

    /**
     * This is intended to be used in raw html to access the binary data of the files
     * @param noteid The files primary key is a composite key consisting of both noteid and filename, this represents the noteid
     * @param filename This represents the filename
     * @return returns the binary data of a file related to a key
     */
    @GetMapping("/{noteid}/{filename}")
    public ResponseEntity<byte[]> getFile(@PathVariable("noteid") long noteid,
            @PathVariable("filename") String filename) {
        FileCompositeKey fck = new FileCompositeKey(filename, noteid);
        FileData fd = repo.findById(fck).orElse(null);
        if (fd != null) {
            return ResponseEntity.ok(fd.getData());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }


    /**
     *
     * @param noteid
     * @return All the names of the files related with the note id provided
     */
    @GetMapping("/{noteid}")
    public ResponseEntity<List<String>> fetchFileName(@PathVariable("noteid") long noteid){
        return ResponseEntity.ok(repo.fetchAllFileNamesById(noteid));
    }

    /**
     * Uploads a file to the repo
     * @param noteid the id of the related note
     * @param file the file to be uploaded
     * @param filename the name of the file
     * @return A success/error message
     * @throws IOException
     */
    @PostMapping("/{noteid}/upload")
    public ResponseEntity<String> postFile(@PathVariable("noteid") long noteid,
            @RequestParam("file") MultipartFile file,
            @RequestParam("filename") String filename) throws IOException {
        try {
            FileData fd = new FileData(filename, file.getBytes(), noteid);
            repo.save(fd);
            return ResponseEntity.ok("File uploaded successfully\n");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed\n");
        }
    }

    /**
     * Deletes a file from the repo given a key
     * @param noteid the noteid part of the key
     * @param filename the filename part of the key
     * @return a success/error message
     */
    @DeleteMapping("/{noteid}/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable("noteid") long noteid,
            @PathVariable("filename") String filename) {
        FileCompositeKey fck = new FileCompositeKey(filename, noteid);
        try {
            repo.deleteById(fck);
            return ResponseEntity.ok("File deleted successfully\n");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting file: " + e.getMessage());
        }
    }

    /**
     * Deletes all files related to a specific noteid
     * @param noteid the noteid we want to delete for
     * @return a success/error message
     */
    @DeleteMapping("/{noteid}/all")
    public ResponseEntity<String> deleteAllRelated(@PathVariable long noteid) {
        try {
            repo.deleteByNoteId(noteid);
            return ResponseEntity.ok("Files successfully deleted\n");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting files: " + e.getMessage());
        }
    }

}
