package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur;

import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Role;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Utilisateur;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.UtilisateurRole;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.UtilisateurRoleId;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.RoleRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.UtilisateurRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.UtilisateurRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurRoleRepository utilisateurRoleRepository;
//    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Créer le rôle ADMIN
        Role adminRole = roleRepository.findByNom("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setNom("ADMIN");
            role.setDescription("Administrateur système");
            return roleRepository.save(role);
        });

        // 2. Créer l'utilisateur admin
        Utilisateur adminUser = utilisateurRepository.findByLogin("admin").orElseGet(() -> {
            Utilisateur user = new Utilisateur();
            user.setLogin("admin");
            user.setMotDePasseHash("admin");
            user.setActif(true);
            return utilisateurRepository.save(user);
        });

        // 3. Assigner le rôle ADMIN à cet utilisateur (si pas déjà fait)
        boolean alreadyAssigned = utilisateurRoleRepository.findByUtilisateurId(adminUser.getUtilisateurId()).stream()
                .anyMatch(ur -> ur.getRole().getId().equals(adminRole.getId()));

        if (!alreadyAssigned) {
            UtilisateurRoleId id = new UtilisateurRoleId(adminUser.getUtilisateurId(), adminRole.getId());
            UtilisateurRole ur = new UtilisateurRole();
            ur.setId(id);
            ur.setUtilisateur(adminUser);
            ur.setRole(adminRole);
            utilisateurRoleRepository.save(ur);
        }

        // 4. Créer le rôle ETUDIANT (indispensable pour l'inscription)
        if (roleRepository.findByNom("ETUDIANT").isEmpty()) {
            Role etudiantRole = new Role();
            etudiantRole.setNom("ETUDIANT");
            etudiantRole.setDescription("Étudiant standard");
            roleRepository.save(etudiantRole);
        }
    }
}