package ma.barid.avis_sort_DGI.service;

import ma.barid.avis_sort_DGI.entity.Cab;
import ma.barid.avis_sort_DGI.entity.Evenement;
import ma.barid.avis_sort_DGI.entity.EvenementCab;
import ma.barid.avis_sort_DGI.entity.FlagIps;
import ma.barid.avis_sort_DGI.repository.CabRepository;
import ma.barid.avis_sort_DGI.repository.EvenementCabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvenementCabService {

    @Autowired
    private EvenementCabRepository evenementCabRepository;

    @Autowired
    private CabRepository cabRepository;

    @Autowired
    private ExportCsvService exportCsvService;

    public EvenementCab ajouterEvenement(Long cabId, Evenement evenement) {

        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new RuntimeException("CAB non trouvé : " + cabId));

        // Mettre à jour flag_ips et statut du CAB selon l'événement
        switch (evenement) {
            case DISTRIBUE  -> {
                cab.setStatut("DISTRIBUE");
                cab.setFlagIps(FlagIps.EXISTE);
                cab.setDateLivraison(LocalDateTime.now());
            }
            case RETOURNE   -> {
                cab.setStatut("RETOURNE");
                cab.setFlagIps(FlagIps.NON_EXISTE);
            }
            case ECHEC      -> {
                cab.setStatut("ECHEC");
                cab.setFlagIps(FlagIps.EXCEPTION);
            }
            case EN_ATTENTE -> {
                cab.setStatut("EN_ATTENTE");
                cab.setFlagIps(FlagIps.NON_EXISTE);
            }
        }
        cabRepository.save(cab);

        // Créer l'événement
        EvenementCab nouvelEvenement = new EvenementCab();
        nouvelEvenement.setCab(cab);
        nouvelEvenement.setEvenement(evenement);
        nouvelEvenement.setDateEvenement(LocalDateTime.now());
        EvenementCab saved = evenementCabRepository.save(nouvelEvenement);

        try {
            exportCsvService.exporterVersDGI();
        } catch (IOException e) {
            System.err.println("Erreur generation REP/OUT : " + e.getMessage());
        }

        return saved;
    }

    public List<EvenementCab> getEvenementsByCabId(Long cabId) {
        return evenementCabRepository.findByCabId(cabId);
    }
}