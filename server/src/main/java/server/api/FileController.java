package server.api;

import commons.FileCompositeKey;
import commons.FileData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.database.FileRepository;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileRepository repo;

    public FileController(FileRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{noteid}/{filename}")
    public ResponseEntity<byte[]> getFile(@PathVariable("noteid") long noteid, @PathVariable("filename") String filename){
        FileCompositeKey fck = new FileCompositeKey(filename, noteid);
        FileData fd = repo.findById(fck).orElse(null);
        if(fd != null){
            return ResponseEntity.ok(fd.getData());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping("/{noteid}/upload")
    public ResponseEntity<String> postFile(@PathVariable("noteid") long noteid, @RequestParam("file") MultipartFile file,
                                           @RequestParam("filename") String filename) throws IOException {
        try {
            FileData fd = new FileData(filename, file.getBytes(), noteid);
            repo.save(fd);
            return ResponseEntity.ok("File uploaded successfully\n");
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed\n");
        }
    }

    @DeleteMapping("/{noteid}/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable("noteid") long noteid, @PathVariable("filename") String filename){
        FileCompositeKey fck = new FileCompositeKey(filename, noteid);
        try{
            repo.deleteById(fck);
            return ResponseEntity.ok("File deleted successfully\n");
        }
        catch (Exception e){
            return ResponseEntity.status(500).body("Error deleting file: " + e.getMessage());
        }
    }

    @DeleteMapping("/{noteid}/all")
    public ResponseEntity<String> deleteAllRelated(@PathVariable long noteid){
        try{
            repo.deleteByNoteId(noteid);
            return ResponseEntity.ok("Files successfully deleted\n");
        }
        catch (Exception e){
            return ResponseEntity.status(500).body("Error deleting files: " + e.getMessage());
        }
    }

}
