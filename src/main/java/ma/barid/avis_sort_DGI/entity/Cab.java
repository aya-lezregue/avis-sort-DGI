package ma.barid.avis_sort_DGI.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cab")
@Data
@NoArgsConstructor
public class Cab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroCab;

    private String bureauDistribution;
    private String nomDestinataire;
    private String adresse;
    private String numeroSequentiel;
    private String identifiantFiscal;
    private LocalDate dateEcheance;
    private LocalDateTime dateImport;

    @Enumerated(EnumType.STRING)
    private FlagIps flagIps;

    private String statut;
}
