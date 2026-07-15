package ma.barid.avis_sort_DGI.controller;

import ma.barid.avis_sort_DGI.service.ExportCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "http://localhost:3000")
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
}