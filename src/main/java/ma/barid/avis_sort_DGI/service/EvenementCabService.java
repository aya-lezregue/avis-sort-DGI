package ma.barid.avis_sort_DGI.service;

import ma.barid.avis_sort_DGI.entity.Cab;
import ma.barid.avis_sort_DGI.entity.Evenement;
import ma.barid.avis_sort_DGI.entity.EvenementCab;
import ma.barid.avis_sort_DGI.entity.FlagIps;
import ma.barid.avis_sort_DGI.repository.CabRepository;
import ma.barid.avis_sort_DGI.repository.EvenementCabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvenementCabService {

    @Autowired
    private EvenementCabRepository evenementCabRepository;

    @Autowired
    private CabRepository cabRepository;

    public EvenementCab ajouterEvenement(Long cabId, Evenement evenement) {

        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new RuntimeException("CAB non trouvé : " + cabId));

        // Mettre à jour flag_ips et statut du CAB selon l'événement
        switch (evenement) {
            case DISTRIBUE  -> {
                cab.setStatut("DISTRIBUE");
                cab.setFlagIps(FlagIps.EXISTE);
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

        return evenementCabRepository.save(nouvelEvenement);
    }

    public List<EvenementCab> getEvenementsByCabId(Long cabId) {
        return evenementCabRepository.findByCabId(cabId);
    }
}