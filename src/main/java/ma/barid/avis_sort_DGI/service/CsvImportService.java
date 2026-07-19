package ma.barid.avis_sort_DGI.service;

import ma.barid.avis_sort_DGI.dto.CsvImportResult;
import ma.barid.avis_sort_DGI.entity.Cab;
import ma.barid.avis_sort_DGI.entity.FlagIps;
import ma.barid.avis_sort_DGI.repository.CabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Service
public class CsvImportService {

    @Autowired
    private CabRepository cabRepository;

    @Value("${rep.in}")
    private String inPath;

    @Value("${rep.archive}")
    private String archivePath;

    @Value("${rep.log}")
    private String logPath;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Point d'entree appele par le controller quand USER_POSTE clique sur "Uploader".
     * 1) ecrit le fichier envoye dans REP/IN
     * 2) le traite immediatement depuis REP/IN
     */
    public CsvImportResult uploaderEtTraiter(MultipartFile file) throws IOException {

        String nomFichier = file.getOriginalFilename();
        if (nomFichier == null || nomFichier.isBlank()) {
            throw new IllegalArgumentException("Nom de fichier invalide");
        }

        Files.createDirectories(Path.of(inPath));
        Path fichierIn = Path.of(inPath, nomFichier);
        Files.copy(file.getInputStream(), fichierIn, StandardCopyOption.REPLACE_EXISTING);

        return traiterFichierDepuisIn(nomFichier);
    }

    /**
     * Traite un fichier deja present physiquement dans REP/IN.
     * Reutilisable aussi si un fichier est depose autrement (ex: DGI direct).
     */
    public CsvImportResult traiterFichierDepuisIn(String nomFichier) throws IOException {

        Path fichierSource = Path.of(inPath, nomFichier);
        if (!Files.exists(fichierSource)) {
            throw new IOException("Fichier introuvable dans REP/IN : " + nomFichier);
        }

        int lignesLues = 0, lignesInserees = 0, lignesDoublons = 0, lignesIgnorees = 0;
        Set<String> vus = new HashSet<>();
        StringBuilder log = new StringBuilder();

        log.append("=== Debut traitement : ").append(nomFichier).append(" ===\n");

        // ISO_8859_1 pour eviter les caracteres casses (accents, etc.) sur des fichiers d'origine Windows
        try (BufferedReader reader = Files.newBufferedReader(fichierSource, StandardCharsets.ISO_8859_1)) {

            String ligne;
            while ((ligne = reader.readLine()) != null) {
                lignesLues++;

                if (ligne.isBlank()) {
                    lignesIgnorees++;
                    log.append("  Ligne ").append(lignesLues).append(" ignoree (vide)\n");
                    continue;
                }

                String[] col = ligne.split("\\|", -1);
                if (col.length < 9) {
                    lignesIgnorees++;
                    log.append("  Ligne ").append(lignesLues).append(" ignoree (colonnes insuffisantes)\n");
                    continue;
                }

                String numeroCab = col[2].trim();
                if (numeroCab.isEmpty()) {
                    lignesIgnorees++;
                    log.append("  Ligne ").append(lignesLues).append(" ignoree (numero_cab vide)\n");
                    continue;
                }

                if (vus.contains(numeroCab)) {
                    lignesDoublons++;
                    log.append("  Doublon dans fichier - numero_cab=").append(numeroCab).append("\n");
                    continue;
                }
                vus.add(numeroCab);

                if (cabRepository.existsByNumeroCab(numeroCab)) {
                    lignesDoublons++;
                    log.append("  Deja en base - numero_cab=").append(numeroCab).append("\n");
                    continue;
                }

                LocalDate dateEcheance = null;
                try {
                    dateEcheance = LocalDate.parse(col[8].trim(), DATE_FMT);
                } catch (Exception e) {
                    log.append("  Date invalide pour numero_cab=").append(numeroCab).append("\n");
                }

                Cab cab = new Cab();
                cab.setNumeroCab(numeroCab); //remplit les champs à partir du CSV
                cab.setBureauDistribution(col[3].trim());
                cab.setNomDestinataire(col[4].trim());
                cab.setAdresse(col[5].trim());
                cab.setNumeroSequentiel(col[6].trim());
                cab.setIdentifiantFiscal(col[7].trim());
                cab.setDateEcheance(dateEcheance);
                cab.setDateImport(LocalDateTime.now());
                cab.setFlagIps(FlagIps.NON_EXISTE);
                cab.setStatut("EN_ATTENTE");
//insertion dans base de donne
                cabRepository.save(cab);
                lignesInserees++;
            }
        }

        log.append("  Lignes lues      : ").append(lignesLues).append("\n");
        log.append("  Lignes inserees  : ").append(lignesInserees).append("\n");
        log.append("  Doublons ignores : ").append(lignesDoublons).append("\n");
        log.append("  Lignes ignorees  : ").append(lignesIgnorees).append("\n");
        log.append("=== Traitement termine : ").append(nomFichier).append(" ===\n");

        Files.createDirectories(Path.of(logPath));
        String nomLog = "traitement_" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".log";
        Files.writeString(Path.of(logPath, nomLog), log.toString(), StandardCharsets.UTF_8);

        Files.createDirectories(Path.of(archivePath));
        Files.move(fichierSource, Path.of(archivePath, nomFichier), StandardCopyOption.REPLACE_EXISTING);

        return new CsvImportResult(nomFichier, lignesLues, lignesInserees, lignesDoublons, lignesIgnorees);
    }
}