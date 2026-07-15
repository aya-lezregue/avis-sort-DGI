package ma.barid.avis_sort_DGI.repository;

import ma.barid.avis_sort_DGI.entity.Details;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetailsRepository extends JpaRepository<Details, Long> {

    List<Details> findByCabId(Long cabId);
}