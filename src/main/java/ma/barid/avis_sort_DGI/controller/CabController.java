package ma.barid.avis_sort_DGI.controller;

import ma.barid.avis_sort_DGI.entity.Cab;
import ma.barid.avis_sort_DGI.service.CabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cabs")
@CrossOrigin(origins = "http://localhost:3000")
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
}