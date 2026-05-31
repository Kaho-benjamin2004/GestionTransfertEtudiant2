package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.CONTROLLER;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.reponse.EtudiantResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.SERVICE.IEtudiantMetier;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.ProfilRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.ResourceNotFoundException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.jwt.UserDetailsImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

//package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.CONTROLLER;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//
//import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.reponse.EtudiantResponseDTO;
//import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.SERVICE.IEtudiantMetier;
//import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.ProfilRequestDTO;
//import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.jwt.UserDetailsImpl;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//
//import java.util.UUID;
//
//@Controller
//@RequestMapping("/etudiant")
//@RequiredArgsConstructor
//@PreAuthorize("hasRole('ETUDIANT')")
//public class EtudiantController {
//
//    private final IEtudiantMetier etudiantMetier;
//
//    private UUID getCurrentEtudiantId() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
//        return etudiantMetier.getEtudiantIdByUtilisateurId(userDetails.getId());
//    }
//
//    @GetMapping("/historique")
//    public String historique(Model model) {
//        UUID etudiantId = getCurrentEtudiantId();
//        EtudiantResponseDTO etudiant = etudiantMetier.consulterHistorique(etudiantId);
//        model.addAttribute("etudiant", etudiant);
//        return "etudiant/historique";
//    }
//
//    @GetMapping("/credits")
//    public String credits(Model model) {
//        UUID etudiantId = getCurrentEtudiantId();
//        model.addAttribute("credits", etudiantMetier.consulterCreditsValides(etudiantId));
//        return "etudiant/credits";
//    }
//
//    @GetMapping("/sanctions")
//    public String sanctions(Model model) {
//        UUID etudiantId = getCurrentEtudiantId();
//        model.addAttribute("sanctions", etudiantMetier.consulterSanctions(etudiantId));
//        return "etudiant/sanctions";
//    }
//
//    @GetMapping("/profil/view")
//    public String viewProfil(Model model) {
//        UUID etudiantId = getCurrentEtudiantId();
//        EtudiantResponseDTO etudiant = etudiantMetier.consulterHistorique(etudiantId);
//        model.addAttribute("etudiant", etudiant);
//        return "etudiant/profil/view";
//    }
//
//    @GetMapping("/profil/edit")
//    public String editProfilForm(Model model) {
//        if (!model.containsAttribute("profilRequest")) {
//            model.addAttribute("profilRequest", new ProfilRequestDTO());
//        }
//        return "etudiant/profil/edit";
//    }
//
//    @PostMapping("/profil/edit")
//    public String updateProfil(@Valid @ModelAttribute("profilRequest") ProfilRequestDTO request,
//                               BindingResult result,
//                               RedirectAttributes redirectAttributes) {
//        if (result.hasErrors()) {
//            return "etudiant/profil/edit";
//        }
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
//        try {
//            etudiantMetier.modifierInformationsPersonnelles(userDetails.getId(), request);
//            redirectAttributes.addFlashAttribute("success", "Profil mis à jour.");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", e.getMessage());
//        }
//        return "redirect:/etudiant/profil/view";
//    }
//}
@Controller
@RequestMapping("/etudiant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
public class EtudiantController {

    private final IEtudiantMetier etudiantMetier;

    private UUID getCurrentEtudiantId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        try {
            return etudiantMetier.getEtudiantIdByUtilisateurId(userDetails.getId());
        } catch (ResourceNotFoundException e) {
            // Créer automatiquement l'étudiant s'il n'existe pas
            etudiantMetier.creerEtudiantPourUtilisateur(userDetails.getId(), null);
            return etudiantMetier.getEtudiantIdByUtilisateurId(userDetails.getId());
        }
    }

    @GetMapping("/credits")
    public String credits(Model model) {
        UUID etudiantId = getCurrentEtudiantId();
        model.addAttribute("credits", etudiantMetier.consulterCreditsValides(etudiantId));
        return "etudiant/credits";
    }

    @GetMapping("/historique")
    public String historique(Model model) {
        UUID etudiantId = getCurrentEtudiantId();
        EtudiantResponseDTO etudiant = etudiantMetier.consulterHistorique(etudiantId);
        model.addAttribute("etudiant", etudiant);
        return "etudiant/historique";
    }

    @GetMapping("/sanctions")
    public String sanctions(Model model) {
        UUID etudiantId = getCurrentEtudiantId();
        model.addAttribute("sanctions", etudiantMetier.consulterSanctions(etudiantId));
        return "etudiant/sanctions";
    }

    @GetMapping("/profil/view")
    public String viewProfil(Model model) {
        UUID etudiantId = getCurrentEtudiantId();
        EtudiantResponseDTO etudiant = etudiantMetier.consulterHistorique(etudiantId);
        model.addAttribute("etudiant", etudiant);
        return "etudiant/profil/view";
    }

    @GetMapping("/profil/edit")
    public String editProfilForm(Model model) {
        if (!model.containsAttribute("profilRequest")) {
            UUID etudiantId = getCurrentEtudiantId();
            EtudiantResponseDTO etudiant = etudiantMetier.consulterHistorique(etudiantId);
            ProfilRequestDTO request = new ProfilRequestDTO();
            request.setNom(etudiant.getNom());
            request.setPrenom(etudiant.getPrenom());
            request.setEmail(etudiant.getEmail());
            request.setTelephone(etudiant.getTelephone());
            request.setMatriculeNational(etudiant.getMatriculeNational());
            model.addAttribute("profilRequest", request);
        }
        return "etudiant/profil/edit";
    }

    @PostMapping("/profil/edit")
    public String updateProfil(@Valid @ModelAttribute("profilRequest") ProfilRequestDTO request,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest httpRequest) {
        if (result.hasErrors()) {
            return "etudiant/profil/edit";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        try {
            etudiantMetier.modifierInformationsPersonnelles(userDetails.getId(), request);
            redirectAttributes.addFlashAttribute("success", "Profil mis à jour.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/etudiant/profil/view";
    }
}