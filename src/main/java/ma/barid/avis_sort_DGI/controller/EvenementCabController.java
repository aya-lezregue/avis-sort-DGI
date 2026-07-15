package ma.barid.avis_sort_DGI.controller;

import ma.barid.avis_sort_DGI.entity.Evenement;
import ma.barid.avis_sort_DGI.entity.EvenementCab;
import ma.barid.avis_sort_DGI.service.EvenementCabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/evenements")
@CrossOrigin(origins = "http://localhost:3000")
public class EvenementCabController {

    @Autowired
    private EvenementCabService evenementCabService;

    // POST ajouter un événement → met à jour le statut du CAB
    @PostMapping("/{cabId}")
    public ResponseEntity<EvenementCab> ajouterEvenement(
            @PathVariable Long cabId,
            @RequestParam Evenement evenement) {
        return ResponseEntity.ok(
                evenementCabService.ajouterEvenement(cabId, evenement)
        );
    }

    // GET tous les événements d'un CAB
    @GetMapping("/{cabId}")
    public List<EvenementCab> getEvenements(@PathVariable Long cabId) {
        return evenementCabService.getEvenementsByCabId(cabId);
    }
}