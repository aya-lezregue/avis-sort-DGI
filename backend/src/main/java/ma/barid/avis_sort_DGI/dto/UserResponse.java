package ma.barid.avis_sort_DGI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ma.barid.avis_sort_DGI.entity.Role;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean actif;
}