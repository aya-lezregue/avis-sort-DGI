package ma.barid.avis_sort_DGI.service;

import ma.barid.avis_sort_DGI.entity.Details;
import ma.barid.avis_sort_DGI.repository.DetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DetailsService {

    @Autowired
    private DetailsRepository detailsRepository;

    // Récupérer les détails d'un CAB
    public List<Details> getDetailsByCabId(Long cabId) {
        return detailsRepository.findByCabId(cabId);
    }

    // Sauvegarder un détail
    public Details saveDetails(Details details) {
        return detailsRepository.save(details);
    }
}