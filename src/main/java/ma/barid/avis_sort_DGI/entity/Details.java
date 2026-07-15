package ma.barid.avis_sort_DGI.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "details")
@Data
@NoArgsConstructor
public class Details {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cab_id", nullable = false)
    private Cab cab;

    private String typeDetail;
    private String valeur;
    private LocalDateTime dateCreation;
    private String observation;
}
