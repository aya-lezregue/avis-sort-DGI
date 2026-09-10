package ma.barid.avis_sort_DGI.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String ancienMotDePasse;
    private String nouveauMotDePasse;
}