package ma.barid.avis_sort_DGI.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "evenement_cab")
@Data
@NoArgsConstructor
public class EvenementCab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cab_id", nullable = false)
    private Cab cab;

    @Enumerated(EnumType.STRING)
    private Evenement evenement;

    private LocalDateTime dateEvenement;
}