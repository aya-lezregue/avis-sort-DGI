package ma.barid.avis_sort_DGI.repository;

import ma.barid.avis_sort_DGI.entity.EvenementCab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvenementCabRepository extends JpaRepository<EvenementCab, Long> {

    List<EvenementCab> findByCabId(Long cabId);
}