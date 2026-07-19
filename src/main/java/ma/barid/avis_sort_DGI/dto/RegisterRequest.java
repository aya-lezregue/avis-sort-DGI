package ma.barid.avis_sort_DGI.dto;

import lombok.Data;
import ma.barid.avis_sort_DGI.entity.Role;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private Role role;
}