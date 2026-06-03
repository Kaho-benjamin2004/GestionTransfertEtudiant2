package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AuthResponseDTO {
    private int statusCode;
    private String message;
    private boolean success;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn; // in seconds
    private UserInfoDTO user; // Nested user object
    
    @Data
    @Builder
    public static class UserInfoDTO {
        private String id;
        private String email;
        private String nom;
        private String prenom;
        private List<String> roles;
    }
}