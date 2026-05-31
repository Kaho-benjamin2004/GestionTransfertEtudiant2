package org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.SERVICE;


import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.TransfertExportDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.request.CoursEquivalentRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.request.DemandeTransfertRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.request.EquivalenceRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.request.PieceJustificativeRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.response.DemandeTransfertResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.BusinessException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ITransfertMetier {
    long getDemandesActivesByEtudiant(UUID etudiantId) throws ResourceNotFoundException;
    List<TransfertExportDTO> getTransfertsForExport(LocalDate dateDebut, LocalDate dateFin, String statut);
    long getTotalTransferts();
    long getTransfertsByStatut(String statut);
    

    // ========== Étudiant ==========
    DemandeTransfertResponseDTO creerDemande(DemandeTransfertRequestDTO request) throws BusinessException;
    List<DemandeTransfertResponseDTO> consulterMesDemandes(UUID etudiantId) throws ResourceNotFoundException;
    void annulerDemande(UUID demandeId, UUID etudiantId) throws ResourceNotFoundException, BusinessException;
    void soumettreDemande(UUID demandeId, UUID etudiantId) throws ResourceNotFoundException, BusinessException;
    void ajouterPieceJustificative(UUID demandeId, PieceJustificativeRequestDTO request, UUID etudiantId) throws ResourceNotFoundException, BusinessException;

    // ========== Université d'origine ==========
    void verifierEligibilite(UUID demandeId) throws ResourceNotFoundException, BusinessException;
    void validerParUniversiteOrigine(UUID demandeId, boolean accepte, String commentaire, UUID responsableId) throws ResourceNotFoundException, BusinessException;

    // ========== Université d'accueil / Commission ==========
    void analyserEquivalences(UUID demandeId, EquivalenceRequestDTO request, UUID responsableId) throws ResourceNotFoundException, BusinessException;
    void validerParUniversiteAccueil(UUID demandeId, boolean accepte, String commentaire, UUID responsableId) throws ResourceNotFoundException, BusinessException;
    void definirCoursReprise(UUID demandeId, List<CoursEquivalentRequestDTO> coursAReprendre, UUID responsableId) throws ResourceNotFoundException, BusinessException;

    // ========== Consultation ==========
    DemandeTransfertResponseDTO suivreDemande(UUID demandeId) throws ResourceNotFoundException;
    List<DemandeTransfertResponseDTO> historiqueTransferts(UUID etudiantId) throws ResourceNotFoundException;

    // ========== Administration centrale ==========
    void detecterDoublons(String numeroEtudiant) throws BusinessException;
    Page<DemandeTransfertResponseDTO> auditerTransferts(Pageable pageable);
}