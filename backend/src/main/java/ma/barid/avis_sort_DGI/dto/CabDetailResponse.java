package ma.barid.avis_sort_DGI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ma.barid.avis_sort_DGI.entity.Cab;
import ma.barid.avis_sort_DGI.entity.EvenementCab;

import java.util.List;

@Data
@AllArgsConstructor
public class CabDetailResponse {
    private Cab cab;
    private List<EvenementCab> historique;
    private String etatActuel;
}