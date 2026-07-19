package ma.barid.avis_sort_DGI.service;

import ma.barid.avis_sort_DGI.entity.Cab;
import ma.barid.avis_sort_DGI.repository.CabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CabService {

    @Autowired
    private CabRepository cabRepository;

    // Récupérer tous les CABs
    public List<Cab> getAllCabs() {
        return cabRepository.findAll();
    }

    // Récupérer un CAB par id
    public Optional<Cab> getCabById(Long id) {
        return cabRepository.findById(id);
    }

    // Récupérer un CAB par numero_cab
    public Optional<Cab> getCabByNumeroCab(String numeroCab) {
        return cabRepository.findByNumeroCab(numeroCab);
    }

    // Sauvegarder un CAB
    public Cab saveCab(Cab cab) {
        return cabRepository.save(cab);
    }

    // Mettre à jour le statut d'un CAB
    public Cab updateStatut(Long id, String statut) {
        Cab cab = cabRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CAB non trouvé : " + id));
        cab.setStatut(statut);
        return cabRepository.save(cab);
    }

    public void deleteCab(Long id) {
        cabRepository.deleteById(id);
    }
}