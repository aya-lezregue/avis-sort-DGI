package ma.barid.avis_sort_DGI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CsvImportResult {
    private String nomFichier;
    private int lignesLues;
    private int lignesInserees;
    private int lignesDoublons;
    private int lignesIgnorees;
}