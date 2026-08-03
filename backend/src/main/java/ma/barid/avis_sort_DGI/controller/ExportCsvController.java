package ma.barid.avis_sort_DGI.controller;

import org.springframework.core.io.Resource;
import ma.barid.avis_sort_DGI.service.ExportCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "http://localhost:4200")
public class ExportCsvController {

    @Autowired
    private ExportCsvService exportCsvService;

    // GET exporter le fichier CSV vers DGI
    @GetMapping("/dgi")
    public ResponseEntity<String> exporterDGI() {
        try {
            exportCsvService.exporterVersDGI();
            return ResponseEntity.ok("Fichier CSV exporté avec succès dans REP/OUT/");
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur export : " + e.getMessage());
        }
    }

    @Value("${rep.out}")
    private String outPath;

    @GetMapping("/list")
    public ResponseEntity<String[]> listerFichiersOut() {
        File dossier = new File(outPath);
        String[] fichiers = dossier.exists()
                ? dossier.list((dir, name) -> name.endsWith(".csv"))
                : new String[0];
        return ResponseEntity.ok(fichiers);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> telechargerFichier(@PathVariable String filename) throws IOException {
        Path base = Path.of(outPath).normalize();
        Path filePath = base.resolve(filename).normalize();

        if (!filePath.startsWith(base)) {
            return ResponseEntity.badRequest().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }


}