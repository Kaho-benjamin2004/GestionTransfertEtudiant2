package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.mapper.AuthMapper;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.ChangementMotDePasseRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.LoginRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.RefreshTokenRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.AuthResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.MessageResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.RefreshTokenResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.JournalConnexion;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Session;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Utilisateur;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.JournalConnexionRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.SessionRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.UtilisateurRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.AuthenticationException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.InvalidTokenException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.ResourceNotFoundException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final int MAX_TENTATIVES = 5;
    private static final int TEMPS_BLOCAGE_MINUTES = 30;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UtilisateurRepository utilisateurRepository;
    private final SessionRepository sessionRepository;
    private final JournalConnexionRepository journalConnexionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request, HttpServletRequest httpRequest) {
        // Support both email and login (username)
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .or(() -> utilisateurRepository.findByLogin(request.getEmail()))
                .orElseThrow(() -> new AuthenticationException("Email/Login ou mot de passe incorrect"));

        if (utilisateur.getBloqueJusqua() != null && utilisateur.getBloqueJusqua().isAfter(LocalDateTime.now())) {
            throw new AuthenticationException("Compte bloqué jusqu'au " + utilisateur.getBloqueJusqua());
        }

        try {
            // Use login field for authentication manager
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(utilisateur.getLogin(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Réinitialiser les tentatives
            utilisateurRepository.reinitialiserTentatives(utilisateur.getLogin());
            utilisateur.setDerniereConnexion(LocalDateTime.now());
            utilisateurRepository.save(utilisateur);

            // Génération tokens
            String accessToken = jwtUtils.generateAccessToken(utilisateur.getUtilisateurId(), utilisateur.getLogin());
            String refreshToken = jwtUtils.generateRefreshToken(utilisateur.getUtilisateurId(), utilisateur.getLogin());

            // Sauvegarde session
            Session session = Session.builder()
                    .token(refreshToken)
                    .adresseIP(httpRequest.getRemoteAddr())
                    .userAgent(httpRequest.getHeader("User-Agent"))
                    .actif(true)
                    .utilisateur(utilisateur)
                    .build();
            sessionRepository.save(session);

            // Journal succès
            journalConnexionRepository.save(JournalConnexion.builder()
                    .succes(true)
                    .adresseIP(httpRequest.getRemoteAddr())
                    .utilisateur(utilisateur)
                    .build());

            long expiresIn = jwtUtils.getExpirationDate(accessToken).getTime() - System.currentTimeMillis();
            return AuthMapper.toAuthResponseDTO(accessToken, refreshToken, expiresIn, utilisateur);

        } catch (Exception e) {
            // Incrémenter les tentatives
            utilisateurRepository.incrementerTentativesEchec(utilisateur.getLogin());

            // Recharger l'utilisateur pour vérifier le seuil
            utilisateurRepository.findByLogin(utilisateur.getLogin()).ifPresent(u -> {
                journalConnexionRepository.save(JournalConnexion.builder()
                        .succes(false)
                        .raisonEchec(e.getMessage())
                        .utilisateur(u)
                        .build());

                if (u.getTentativeEchec() >= MAX_TENTATIVES) {
                    LocalDateTime finBlocage = LocalDateTime.now().plusMinutes(TEMPS_BLOCAGE_MINUTES);
                    utilisateurRepository.bloquerUtilisateur(utilisateur.getLogin(), finBlocage);
                    journalConnexionRepository.save(JournalConnexion.builder()
                            .succes(false)
                            .raisonEchec("Trop de tentatives - compte bloqué " + TEMPS_BLOCAGE_MINUTES + " min")
                            .utilisateur(u)
                            .build());
                }
            });
            throw new AuthenticationException("Email/Login ou mot de passe incorrect");
        }
    }

    @Transactional
    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new InvalidTokenException("Token invalide");
        }
        String login = jwtUtils.getLoginFromToken(refreshToken);
        Utilisateur utilisateur = utilisateurRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", login));

        // Invalider l'ancienne session via la méthode du repository
        sessionRepository.invaliderSession(refreshToken, LocalDateTime.now());

        // Générer nouveaux tokens
        String newAccessToken = jwtUtils.generateAccessToken(utilisateur.getUtilisateurId(), utilisateur.getLogin());
        String newRefreshToken = jwtUtils.generateRefreshToken(utilisateur.getUtilisateurId(), utilisateur.getLogin());

        Session newSession = Session.builder()
                .token(newRefreshToken)
                .utilisateur(utilisateur)
                .actif(true)
                .build();
        sessionRepository.save(newSession);

        long expiresIn = jwtUtils.getExpirationDate(newAccessToken).getTime() - System.currentTimeMillis();
        return AuthMapper.toRefreshTokenResponseDTO(newAccessToken, newRefreshToken, expiresIn);
    }

    @Transactional
    public void changePassword(UUID userId, ChangementMotDePasseRequestDTO request) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), utilisateur.getMotDePasseHash())) {
            throw new AuthenticationException("Mot de passe actuel incorrect");
        }
        
        // Verify password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AuthenticationException("Les mots de passe ne correspondent pas");
        }
        
        utilisateur.setMotDePasseHash(passwordEncoder.encode(request.getNewPassword()));
        utilisateur.setTentativeEchec(0);
        utilisateur.setBloqueJusqua(null);
        utilisateurRepository.save(utilisateur);

        // Invalider toutes les sessions de l'utilisateur
        sessionRepository.findAll().stream()
                .filter(s -> s.getUtilisateur().getUtilisateurId().equals(userId) && s.getActif())
                .forEach(s -> sessionRepository.invaliderSession(s.getToken(), LocalDateTime.now()));
        
        MessageResponseDTO.builder()
                .message("Mot de passe changé avec succès")
                .success(true)
                .statusCode(200)
                .build();
    }

    @Transactional
    public void logout(UUID userId, String refreshToken) {
        sessionRepository.findByToken(refreshToken).ifPresent(session -> {
            if (session.getUtilisateur().getUtilisateurId().equals(userId)) {
                sessionRepository.invaliderSession(refreshToken, LocalDateTime.now());
            }
        });
    }
}