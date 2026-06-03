package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.SERVICE.IEtudiantMetier;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.mapper.RoleMapper;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.mapper.UtilisateurMapper;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.ProfilRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.RegisterUserRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.UtilisateurRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.UtilisateurUpdateRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.*;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Profil;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Role;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Utilisateur;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.ProfilRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.RoleRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.UtilisateurRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.UtilisateurRoleRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.AlreadyExistsException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.ResourceNotFoundException;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class UtilisateurService {
    private  final  AuthService authService;
    @Lazy
    private final IEtudiantMetier etudiantMetier;
    private final UtilisateurRepository utilisateurRepository;
    private final ProfilRepository profilRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurRoleService utilisateurRoleService;
    private final UtilisateurRoleRepository utilisateurRoleRepository;



    // Dans UtilisateurService.java
    @Transactional
    public void updateProfil(UUID userId, ProfilRequestDTO profilRequest) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));
        Profil profil = utilisateur.getProfil();
        if (profil == null) {
            profil = new Profil();
            profil.setUtilisateur(utilisateur);
            utilisateur.setProfil(profil);
        }
        profil.setNom(profilRequest.getNom());
        profil.setPrenom(profilRequest.getPrenom());
        profil.setEmail(profilRequest.getEmail());
        profil.setTelephone(profilRequest.getTelephone());
        profil.setMatriculeNational(profilRequest.getMatriculeNational());

        profilRepository.save(profil);
    }
   

//    @Transactional
//    public void createUser(UtilisateurRequestDTO request) {
//        if (utilisateurRepository.existsByLogin(request.getLogin())) {
//            throw new AlreadyExistsException("Login déjà utilisé");
//        }
//        if (profilRepository.findByEmail(request.getEmail()).isPresent()) {
//            throw new AlreadyExistsException("Email déjà utilisé");
//        }
//
//        // Création de l'utilisateur
//        Utilisateur utilisateur = new Utilisateur();
//        utilisateur.setLogin(request.getLogin());
//        utilisateur.setMotDePasseHash(passwordEncoder.encode(request.getMotDePasse()));
//        utilisateur.setActif(request.getActif() != null ? request.getActif() : true);
//        utilisateur = utilisateurRepository.save(utilisateur);
//
//        // Création du profil
//        Profil profil = new Profil();
//        profil.setNom(request.getNom());
//        profil.setPrenom(request.getPrenom());
//        profil.setEmail(request.getEmail());
//        profil.setTelephone(request.getTelephone());
//        profil.setMatriculeNational(request.getMatriculeNational());
//        profil.setUtilisateur(utilisateur);
//        profilRepository.save(profil);
//        utilisateur.setProfil(profil);
//        utilisateur = utilisateurRepository.save(utilisateur); // mise à jour
//
//        // Assignation des rôles
//        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
//            for (UUID roleId : request.getRoleIds()) {
//                utilisateurRoleService.assignRoleToUser(utilisateur.getUtilisateurId(), roleId);
//            }
//        } else {
//            // Rôle par défaut (ÉTUDIANT)
//            Role etudiantRole = roleRepository.findByNom("ETUDIANT")
//                    .orElseThrow(() -> new ResourceNotFoundException("Rôle ETUDIANT non trouvé"));
//            utilisateurRoleService.assignRoleToUser(utilisateur.getUtilisateurId(), etudiantRole.getId());
//        }
//
//        UtilisateurMapper.toDTO(utilisateur);
//    }
    @Transactional
    public void createUser(UtilisateurRequestDTO request) {
        if (utilisateurRepository.existsByLogin(request.getLogin())) {
            throw new AlreadyExistsException("Login déjà utilisé");
        }
        if (profilRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Email déjà utilisé");
        }

        // Création de l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin(request.getLogin());
        utilisateur.setMotDePasseHash(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setActif(request.getActif() != null ? request.getActif() : true);
        utilisateur = utilisateurRepository.save(utilisateur);

        // Création du profil
        Profil profil = new Profil();
        profil.setNom(request.getNom());
        profil.setPrenom(request.getPrenom());
        profil.setEmail(request.getEmail());
        profil.setTelephone(request.getTelephone());
        profil.setMatriculeNational(request.getMatriculeNational());
        profil.setUtilisateur(utilisateur);
        profilRepository.save(profil);
        utilisateur.setProfil(profil);
        utilisateur = utilisateurRepository.save(utilisateur); // mise à jour

        // Assignation des rôles
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            for (UUID roleId : request.getRoleIds()) {
                utilisateurRoleService.assignRoleToUser(utilisateur.getUtilisateurId(), roleId);
            }
        } else {
            // Rôle par défaut (ÉTUDIANT)
            Role etudiantRole = roleRepository.findByNom("ETUDIANT")
                    .orElseThrow(() -> new ResourceNotFoundException("Rôle ETUDIANT non trouvé"));
            utilisateurRoleService.assignRoleToUser(utilisateur.getUtilisateurId(), etudiantRole.getId());
        }

        // Créer automatiquement l'entité Étudiant si le rôle ETUDIANT a été attribué
        // (si le rôle par défaut ou si la liste des rôles contient ETUDIANT)
        boolean hasEtudiantRole = request.getRoleIds() == null || request.getRoleIds().isEmpty() ||
                (request.getRoleIds() != null && request.getRoleIds().stream()
                        .anyMatch(roleId -> {
                            Role r = roleRepository.findById(roleId).orElse(null);
                            return r != null && "ETUDIANT".equals(r.getNom());
                        }));
        if (hasEtudiantRole) {
            try {
                // Créer un numéro étudiant par défaut (basé sur l'ID)
                String numeroEtudiant = "ETU" + utilisateur.getUtilisateurId().toString().substring(0, 8);
                etudiantMetier.creerEtudiantPourUtilisateur(utilisateur.getUtilisateurId(), numeroEtudiant);
            } catch (Exception e) {
                log.warn("Impossible de créer l'étudiant : {}", e.getMessage());
            }
        }
    }

    @Transactional
    public void createUserFromRegistration(RegisterUserRequestDTO request) {
        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }

        // Check if email already exists
        if (profilRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Email déjà utilisé");
        }

        // Generate login from email (first part before @)
        String generatedLogin = request.getEmail().split("@")[0];
        int counter = 0;
        String finalLogin = generatedLogin;
        while (utilisateurRepository.existsByLogin(finalLogin)) {
            counter++;
            finalLogin = generatedLogin + counter;
        }

        // Create user
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin(finalLogin);
        utilisateur.setMotDePasseHash(passwordEncoder.encode(request.getPassword()));
        utilisateur.setActif(true);
        utilisateur = utilisateurRepository.save(utilisateur);

        // Create profile
        Profil profil = new Profil();
        profil.setNom(request.getNom());
        profil.setPrenom(request.getPrenom());
        profil.setEmail(request.getEmail());
        profil.setUtilisateur(utilisateur);
        profilRepository.save(profil);
        utilisateur.setProfil(profil);
        utilisateur = utilisateurRepository.save(utilisateur);

        // Assign role
        String roleName = request.getRole() != null ? request.getRole() : "ETUDIANT";
        Role role = roleRepository.findByNom(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle", roleName));
        utilisateurRoleService.assignRoleToUser(utilisateur.getUtilisateurId(), role.getId());

        // Create student entity if ETUDIANT role
        if ("ETUDIANT".equals(roleName)) {
            try {
                String numeroEtudiant = "ETU" + utilisateur.getUtilisateurId().toString().substring(0, 8);
                etudiantMetier.creerEtudiantPourUtilisateur(utilisateur.getUtilisateurId(), numeroEtudiant);
            } catch (Exception e) {
                log.warn("Impossible de créer l'étudiant : {}", e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public UtilisateurDetailResponseDTO getUserById(UUID id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
        return UtilisateurMapper.toDetailDTO(utilisateur);
    }

    @Transactional(readOnly = true)
    public Page<UtilisateurSimpleResponseDTO> getAllUsers(Pageable pageable) {
        return utilisateurRepository.findAll(pageable)
                .map(UtilisateurMapper::toSimpleDTO);
    }

    @Transactional
    public UtilisateurResponseDTO updateUser(UUID id, UtilisateurUpdateRequestDTO request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
        if (request.getLogin() != null) utilisateur.setLogin(request.getLogin());
        if (request.getActif() != null) utilisateur.setActif(request.getActif());
        utilisateur = utilisateurRepository.save(utilisateur);
        return UtilisateurMapper.toDTO(utilisateur);
    }

    @Transactional
    public MessageResponseDTO deleteUser(UUID id) {
        if (!utilisateurRepository.existsById(id)) throw new ResourceNotFoundException("Utilisateur", id);
        utilisateurRepository.deleteById(id);
        return MessageResponseDTO.builder().message("Utilisateur supprimé").success(true).statusCode(200).build();
    }

    // Utilisation de UtilisateurRoleRepository
    public List<String> getUserRoleNames(UUID userId) {
        return utilisateurRoleRepository.findRoleNomsByUtilisateurId(userId);
    }

    public List<RoleResponseDTO> getUserRoles(UUID userId) {
        return utilisateurRoleRepository.findByUtilisateurId(userId).stream()
                .map(ur -> RoleMapper.toDTO(ur.getRole()))
                .collect(Collectors.toList());
    }


}