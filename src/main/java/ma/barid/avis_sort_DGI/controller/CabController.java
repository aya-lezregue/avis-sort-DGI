package ma.barid.avis_sort_DGI.controller;

import ma.barid.avis_sort_DGI.dto.CabDetailResponse;
import ma.barid.avis_sort_DGI.dto.CsvImportResult;
import ma.barid.avis_sort_DGI.entity.Cab;
import ma.barid.avis_sort_DGI.service.CabService;
import ma.barid.avis_sort_DGI.service.CsvImportService;
import ma.barid.avis_sort_DGI.service.EvenementCabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cabs")
@CrossOrigin(origins = "http://localhost:4200")
public class CabController {

    @Autowired
    private CabService cabService;


    // GET tous les CABs
    @GetMapping
    public List<Cab> getAllCabs() {
        return cabService.getAllCabs();
    }

    // GET un CAB par id
    @GetMapping("/{id}")
    public ResponseEntity<Cab> getCabById(@PathVariable Long id) {
        return cabService.getCabById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET un CAB par numero_cab
    @GetMapping("/numero/{numeroCab}")
    public ResponseEntity<Cab> getCabByNumeroCab(@PathVariable String numeroCab) {
        return cabService.getCabByNumeroCab(numeroCab)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT mettre à jour le statut
    @PutMapping("/{id}/statut")
    public ResponseEntity<Cab> updateStatut(
            @PathVariable Long id,
            @RequestParam String statut) {
        return ResponseEntity.ok(cabService.updateStatut(id, statut));
    }

    @Autowired
    private CsvImportService csvImportService;

    @PostMapping("/upload")
    public ResponseEntity<CsvImportResult> uploadCsv(@RequestParam("file") MultipartFile file) throws IOException {
        CsvImportResult result = csvImportService.uploaderEtTraiter(file);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/traiter/{nomFichier}")
    public ResponseEntity<CsvImportResult> traiterFichier(@PathVariable String nomFichier) throws IOException {
        CsvImportResult result = csvImportService.traiterFichierDepuisIn(nomFichier);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCab(@PathVariable Long id) {
        cabService.deleteCab(id);
        return ResponseEntity.noContent().build();
    }

    @Autowired
    private EvenementCabService evenementCabService;

    @GetMapping("/numero/{numeroCab}/detail")
    public ResponseEntity<CabDetailResponse> getDetailByNumeroCab(@PathVariable String numeroCab) {
        return cabService.getCabByNumeroCab(numeroCab)
                .map(cab -> {
                    var historique = evenementCabService.getEvenementsByCabId(cab.getId());
                    return ResponseEntity.ok(new CabDetailResponse(cab, historique, cab.getStatut()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/numero/{numeroCab}/statut")
    public ResponseEntity<Cab> updateStatutByNumeroCab(
            @PathVariable String numeroCab,
            @RequestParam String statut) {

        return ResponseEntity.ok(
                cabService.updateStatutByNumeroCab(numeroCab, statut)
        );
    }

}