package ma.barid.avis_sort_DGI.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/depot")
@CrossOrigin(origins = "http://localhost:4200")
public class DepotController {

    @Value("${rep.in}")
    private String inPath;

    @Value("${rep.archive}")
    private String archivePath;

    @PostMapping("/csv")
    public ResponseEntity<String> deposerFichier(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Fichier vide");
        }

        File dossier = new File(inPath);
        if (!dossier.exists()) dossier.mkdirs();

        String nomFichier = file.getOriginalFilename();
        Path destination = Path.of(inPath, nomFichier);

        if (Files.exists(destination)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Un fichier nommé \"" + nomFichier + "\" est déjà en attente de traitement. " +
                            "Renommez votre fichier ou attendez qu'il soit traité par la Poste.");
        }

        Path destinationArchive = Path.of(archivePath, nomFichier);
        if (Files.exists(destinationArchive)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Un fichier nommé \"" + nomFichier + "\" a déjà été traité précédemment (archivé). " +
                            "Si ce sont de nouvelles données, renommez le fichier.");
        }

        Files.copy(file.getInputStream(), destination);

        return ResponseEntity.ok("Fichier deposé : " + nomFichier);
    }

    @GetMapping("/pending")
    public ResponseEntity<String[]> fichiersEnAttente() {
        File dossier = new File(inPath);
        String[] fichiers = dossier.exists()
                ? dossier.list((dir, name) -> name.endsWith(".csv"))
                : new String[0];
        return ResponseEntity.ok(fichiers);
    }
}