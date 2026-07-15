package ma.barid.avis_sort_DGI.repository;

import ma.barid.avis_sort_DGI.entity.Cab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CabRepository extends JpaRepository<Cab, Long> {

    boolean existsByNumeroCab(String numeroCab);
    Optional<Cab> findByNumeroCab(String numeroCab);
}