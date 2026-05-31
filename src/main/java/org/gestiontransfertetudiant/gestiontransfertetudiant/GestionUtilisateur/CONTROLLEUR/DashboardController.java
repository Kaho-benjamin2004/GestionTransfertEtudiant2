//package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.CONTROLLEUR;
//
//import lombok.RequiredArgsConstructor;
//import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.UtilisateurDetailResponseDTO;
//import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.UtilisateurService;
//import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.jwt.UserDetailsImpl;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.Collection;
//import java.util.UUID;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/dashboard")
//public class DashboardController {
//
//    private final UtilisateurService utilisateurService;
//
//    @GetMapping
//    public String dashboard(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
//            return "redirect:/auth/login";
//        }
//        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
//        UUID userId = userDetails.getId();
//        UtilisateurDetailResponseDTO user = utilisateurService.getUserById(userId);
//        model.addAttribute("user", user);
//
//        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
//        boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//        boolean isAgent = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
//        boolean isCommission = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_COMMISSION"));
//        boolean isUniv = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_UNIV_A") || a.getAuthority().equals("ROLE_UNIV_B"));
//
//        if (isAdmin) {
//            return "dashboard/admin";
//        } else if (isAgent) {
//            return "dashboard/agent";
//        } else if (isCommission) {
//            return "dashboard/commission";
//        } else if (isUniv) {
//            return "dashboard/university";
//        } else {
//            return "dashboard/student";
//        }
//    }
//}
package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.CONTROLLEUR;

import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.SERVICE.ITransfertMetier;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.SERVICE.IEtudiantMetier;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.UtilisateurDetailResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.UtilisateurService;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.jwt.UserDetailsImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final UtilisateurService utilisateurService;

    @Lazy
    private final IEtudiantMetier etudiantMetier;

    @Lazy
    private final ITransfertMetier transfertMetier;

    @GetMapping
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            return "redirect:/auth/login";
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        UUID userId = userDetails.getId();
        UtilisateurDetailResponseDTO user = utilisateurService.getUserById(userId);
        model.addAttribute("user", user);

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAgent = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
        boolean isCommission = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_COMMISSION"));
        boolean isUniv = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_UNIV_A") || a.getAuthority().equals("ROLE_UNIV_B"));
        boolean isEtudiant = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ETUDIANT"));

        if (isEtudiant) {
            try {
                UUID etudiantId = etudiantMetier.getEtudiantIdByUtilisateurId(userId);
                model.addAttribute("moyenneGenerale", etudiantMetier.calculerMoyenneGenerale(etudiantId));
                model.addAttribute("creditsEcts", etudiantMetier.getTotalCreditsAcquis(etudiantId));
                model.addAttribute("demandesActives", transfertMetier.getDemandesActivesByEtudiant(etudiantId));
            } catch (Exception e) {
                model.addAttribute("moyenneGenerale", 0);
                model.addAttribute("creditsEcts", 0);
                model.addAttribute("demandesActives", 0);
            }
        }

        if (isAdmin) {
            return "dashboard/admin";
        } else if (isAgent) {
            return "dashboard/agent";
        } else if (isCommission) {
            return "dashboard/commission";
        } else if (isUniv) {
            return "dashboard/university";
        } else {
            return "dashboard/student";
        }
    }
}