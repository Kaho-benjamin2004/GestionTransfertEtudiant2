package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.CONTROLLEUR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.RegistrationDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.ChangementMotDePasseRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.LoginRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.RefreshTokenRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.AuthResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.MessageResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.AuthService;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.UtilisateurService;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.RestSecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Gestion de l'accès, des sessions et des mots de passe")
public class AuthRestController {

    private final AuthService authService;
    private final UtilisateurService utilisateurService;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie l'utilisateur et retourne un jeu de tokens JWT.")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                                                 HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(authService.login(request, httpServletRequest));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchir le token", description = "Génère un nouveau token d'accès à partir d'un refresh token valide.")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Créer un compte", description = "Enregistre un nouvel utilisateur dans le système.")
    public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody RegistrationDTO request) {
        utilisateurService.createUserFromRegistration(request.getRegisterRequest());
        return ResponseEntity.ok(MessageResponseDTO.builder()
                .message("Inscription réussie. Veuillez vous connecter.")
                .success(true)
                .statusCode(200)
                .build());
    }

    @PostMapping("/change-password")
    @Operation(summary = "Changer le mot de passe", description = "Met à jour le mot de passe de l'utilisateur connecté.")
    public ResponseEntity<MessageResponseDTO> changePassword(@AuthenticationPrincipal Object principal,
                                                             @Valid @RequestBody ChangementMotDePasseRequestDTO request) {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(MessageResponseDTO.builder()
                    .message("Utilisateur non authentifié")
                    .success(false)
                    .statusCode(401)
                    .build());
        }
        authService.changePassword(userId, request);
        return ResponseEntity.ok(MessageResponseDTO.builder()
                .message("Mot de passe changé avec succès")
                .success(true)
                .statusCode(200)
                .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion", description = "Invalide le refresh token de l'utilisateur connecté.")
    public ResponseEntity<MessageResponseDTO> logout(@AuthenticationPrincipal Object principal,
                                                     @RequestParam String refreshToken) {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(MessageResponseDTO.builder()
                    .message("Utilisateur non authentifié")
                    .success(false)
                    .statusCode(401)
                    .build());
        }
        authService.logout(userId, refreshToken);
        return ResponseEntity.ok(MessageResponseDTO.builder()
                .message("Utilisateur déconnecté")
                .success(true)
                .statusCode(200)
                .build());
    }
}
