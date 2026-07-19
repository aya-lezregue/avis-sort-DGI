package ma.barid.avis_sort_DGI.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/depot")
@CrossOrigin(origins = "http://localhost:4200")
public class DepotController {

    @Value("${rep.in}")
    private String inPath;

    @PostMapping("/csv")
    public ResponseEntity<String> deposerFichier(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Fichier vide");
        }
        File dossier = new File(inPath);
        if (!dossier.exists()) dossier.mkdirs();

        Path destination = Path.of(inPath, file.getOriginalFilename());
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok("Fichier depose : " + file.getOriginalFilename());
    }
//les fichiers qui se trouve dans rep/in
    @GetMapping("/pending")
    public ResponseEntity<String[]> fichiersEnAttente() {
        File dossier = new File(inPath);
        String[] fichiers = dossier.exists()
                ? dossier.list((dir, name) -> name.endsWith(".csv"))
                : new String[0];
        return ResponseEntity.ok(fichiers);
    }
}