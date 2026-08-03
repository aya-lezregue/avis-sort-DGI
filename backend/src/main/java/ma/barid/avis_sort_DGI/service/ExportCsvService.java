package ma.barid.avis_sort_DGI.service;

import ma.barid.avis_sort_DGI.entity.Cab;
import ma.barid.avis_sort_DGI.repository.CabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportCsvService {

    @Autowired
    private CabRepository cabRepository;

    @Value("${rep.out}")
    private String outPath;

    public File exporterVersDGI() throws IOException {

        List<Cab> cabs = cabRepository.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String nomFichier = outPath + "/EXPORT_DGI_" + LocalDate.now() + ".csv";
        File fichier = new File(nomFichier);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fichier))) {

            // Entête
            bw.write("numero_cab|bureau_distribution|nom_destinataire" +
                    "|adresse|numero_sequentiel|identifiant_fiscal" +
                    "|date_echeance|statut");
            bw.newLine();

            // Lignes
            for (Cab cab : cabs) {
                bw.write(
                        cab.getNumeroCab() + "|" +
                                cab.getBureauDistribution() + "|" +
                                cab.getNomDestinataire() + "|" +
                                cab.getAdresse() + "|" +
                                cab.getNumeroSequentiel() + "|" +
                                cab.getIdentifiantFiscal() + "|" +
                                (cab.getDateEcheance() != null ?
                                        cab.getDateEcheance().format(fmt) : "") + "|" +
                                cab.getStatut()
                );
                bw.newLine();
            }
        }
        return fichier;
    }
}