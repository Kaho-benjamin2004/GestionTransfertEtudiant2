package org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.SERVICE;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.TransfertExportDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.mapper.CoursEquivalentMapper;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.mapper.DemandeTransfertMapper;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.mapper.EquivalenceMapper;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.mapper.PieceJustificativeMapper;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.request.*;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.response.DemandeTransfertResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.entity.*;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.repository.*;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.entity.Etudiant;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.repository.EtudiantRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.BusinessException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TransfertMetierImpl implements ITransfertMetier {

    private final DemandeTransfertRepository demandeRepository;
    private final PieceJustificativeRepository pieceRepository;
    private final EquivalenceRepository equivalenceRepository;
    private final CoursEquivalentRepository coursEquivalentRepository;
    private final TransfertAuditRepository auditRepository;
    private final EtudiantRepository etudiantRepository;

    // ========== Méthodes pour supervision ==========
    @Override
    @Transactional(readOnly = true)
    public long getTotalTransferts() {
        return demandeRepository.count();
    }




    @Override
    @Transactional(readOnly = true)
    public long getTransfertsByStatut(String statut) {
        return demandeRepository.countByStatut(statut);
    }

//    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public List<TransfertExportDTO> getTransfertsForExport(LocalDate dateDebut, LocalDate dateFin, String statut) {
//        Specification<DemandeTransfert> spec = (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            if (dateDebut != null && dateFin != null) {
//                predicates.add(cb.between(root.get("dateDemande"), dateDebut, dateFin));
//            }
//            if (statut != null && !statut.isEmpty()) {
//                predicates.add(cb.equal(root.get("statut"), statut));
//            }
//            return cb.and(predicates.toArray(new Predicate[0]));
//        };
//        List<DemandeTransfert> demandes = demandeRepository.findAll((Sort) spec);
//        return demandes.stream()
//                .map(d -> TransfertExportDTO.builder()
//                        .id(d.getId())
//                        .etudiantNom(d.getEtudiant().getUtilisateur().getProfil().getNom())
//                        .etudiantPrenom(d.getEtudiant().getUtilisateur().getProfil().getPrenom())
//                        .dateDemande(d.getDateDemande())
//                        .motif(d.getMotif())
//                        .statut(d.getStatut())
//                        .universiteOrigine(extraireUniversiteOrigine(d))
//                        .universiteCible(extraireUniversiteCible(d))
//                        .build())
//                .collect(Collectors.toList());
//    }


    @Override
    @Transactional(readOnly = true)
    public long getDemandesActivesByEtudiant(UUID etudiantId) throws ResourceNotFoundException {
        if (!etudiantRepository.existsById(etudiantId)) {
            throw new ResourceNotFoundException("Étudiant", etudiantId);
        }
        return demandeRepository.countByEtudiantIdAndStatutNotIn(etudiantId, List.of("ACCEPTEE", "REFUSEE", "ANNULEE"));
    }

    //    private String extraireUniversiteOrigine(DemandeTransfert demande) {
//        // À adapter selon votre logique métier (ex: depuis l'établissement de l'étudiant)
//        return "Université d'origine (non renseignée)";
//    }
@Override
@Transactional(readOnly = true)
public List<TransfertExportDTO> getTransfertsForExport(LocalDate dateDebut, LocalDate dateFin, String statut) {
    List<DemandeTransfert> demandes;
    if (dateDebut != null && dateFin != null) {
        demandes = demandeRepository.findByDateDemandeBetween(dateDebut, dateFin);
    } else {
        demandes = demandeRepository.findAll();
    }
    if (statut != null && !statut.isEmpty()) {
        demandes = demandes.stream().filter(d -> statut.equals(d.getStatut())).collect(Collectors.toList());
    }
    return demandes.stream()
            .map(d -> TransfertExportDTO.builder()
                    .id(d.getId())
                    .etudiantNom(d.getEtudiant().getUtilisateur().getProfil().getNom())
                    .etudiantPrenom(d.getEtudiant().getUtilisateur().getProfil().getPrenom())
                    .dateDemande(d.getDateDemande())
                    .motif(d.getMotif())
                    .statut(d.getStatut())
                    .universiteOrigine("Université d'origine")   // valeur par défaut
                    .universiteCible("Université d'accueil")     // valeur par défaut
                    .build())
            .collect(Collectors.toList());
}

    // ========== Étudiant ==========
    @Override
    public DemandeTransfertResponseDTO creerDemande(DemandeTransfertRequestDTO request) throws BusinessException {
        Etudiant etudiant = etudiantRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", request.getEtudiantId()));

        // Vérifier qu'il n'y a pas de demande en cours
        boolean hasActive = demandeRepository.findByEtudiantId(etudiant.getId()).stream()
                .anyMatch(d -> !List.of("ACCEPTEE", "REFUSEE", "ANNULEE").contains(d.getStatut()));
        if (hasActive) {
            throw new BusinessException("Vous avez déjà une demande de transfert en cours", "DUPLICATE_ACTIVE");
        }

        DemandeTransfert demande = DemandeTransfertMapper.toEntity(request, etudiant);
        demande = demandeRepository.save(demande);
        ajouterAudit(demande.getId(), "CREATION", "Demande créée par l'étudiant", etudiant.getUtilisateur().getUtilisateurId());
        return DemandeTransfertMapper.toDTO(demande);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeTransfertResponseDTO> consulterMesDemandes(UUID etudiantId) throws ResourceNotFoundException {
        if (!etudiantRepository.existsById(etudiantId)) {
            throw new ResourceNotFoundException("Étudiant", etudiantId);
        }
        return demandeRepository.findByEtudiantId(etudiantId).stream()
                .map(DemandeTransfertMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void annulerDemande(UUID demandeId, UUID etudiantId) throws ResourceNotFoundException, BusinessException {
        DemandeTransfert demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId));
        if (!demande.getEtudiant().getId().equals(etudiantId)) {
            throw new BusinessException("Vous n'êtes pas autorisé à annuler cette demande", "FORBIDDEN");
        }
        if (!List.of("SOUMISE", "BROUILLON").contains(demande.getStatut())) {
            throw new BusinessException("Seules les demandes en brouillon ou soumises peuvent être annulées", "INVALID_STATUT");
        }
        demande.setStatut("ANNULEE");
        demandeRepository.save(demande);
        ajouterAudit(demandeId, "ANNULATION", "Demande annulée par l'étudiant", etudiantId);
    }

    @Override
    public void soumettreDemande(UUID demandeId, UUID etudiantId) throws ResourceNotFoundException, BusinessException {
        DemandeTransfert demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId));
        if (!demande.getEtudiant().getId().equals(etudiantId)) {
            throw new BusinessException("Vous n'êtes pas autorisé à soumettre cette demande", "FORBIDDEN");
        }
        if (!"BROUILLON".equals(demande.getStatut())) {
            throw new BusinessException("Seule une demande en brouillon peut être soumise", "INVALID_STATUT");
        }
        demande.setStatut("SOUMISE");
        demandeRepository.save(demande);
        ajouterAudit(demandeId, "SOUMISSION", "Demande soumise par l'étudiant", etudiantId);
    }

    @Override
    public void ajouterPieceJustificative(UUID demandeId, PieceJustificativeRequestDTO request, UUID etudiantId) throws ResourceNotFoundException, BusinessException {
        DemandeTransfert demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId));
        if (!demande.getEtudiant().getId().equals(etudiantId)) {
            throw new BusinessException("Vous n'êtes pas autorisé à ajouter des pièces à cette demande", "FORBIDDEN");
        }
        if (!List.of("BROUILLON", "SOUMISE").contains(demande.getStatut())) {
            throw new BusinessException("Impossible d'ajouter des pièces à ce stade", "INVALID_STATUT");
        }
        String chemin = "/uploads/" + request.getFichier().getOriginalFilename();
        PieceJustificative piece = PieceJustificativeMapper.toEntity(request, demande, chemin);
        pieceRepository.save(piece);
        ajouterAudit(demandeId, "AJOUT_PIECE", "Ajout d'une pièce justificative : " + request.getType(), etudiantId);
    }

    // ========== Université d'origine ==========
    @Override
    @Transactional(readOnly = true)
    public void verifierEligibilite(UUID demandeId) throws ResourceNotFoundException, BusinessException {
        DemandeTransfert demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId));
        // Simuler une vérification
        boolean eligible = true;
        if (!eligible) {
            throw new BusinessException("L'étudiant n'est pas éligible au transfert", "NOT_ELIGIBLE");
        }
    }

    @Override
    public void validerParUniversiteOrigine(UUID demandeId, boolean accepte, String commentaire, UUID responsableId) throws ResourceNotFoundException, BusinessException {
        DemandeTransfert demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId));
        if (!"SOUMISE".equals(demande.getStatut())) {
            throw new BusinessException("La demande doit être à l'état SOUMISE", "INVALID_STATUT");
        }
        if (accepte) {
            demande.setStatut("EN_COURS_ORIGINE");
            demande.setCommentaireRefus(null);
        } else {
            demande.setStatut("REFUSEE");
            demande.setCommentaireRefus(commentaire);
        }
        demandeRepository.save(demande);
        ajouterAudit(demandeId, "AVIS_ORIGINE", "Université d'origine : " + (accepte ? "accepté" : "refusé") + (commentaire != null ? " - " + commentaire : ""), responsableId);
    }

    // ========== Université d'accueil / Commission ==========
    @Override
    @Transactional(readOnly = true)
    public void analyserEquivalences(UUID demandeId, EquivalenceRequestDTO request, UUID responsableId) throws ResourceNotFoundException, BusinessException {
        DemandeTransfert demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId));
        if (!"EN_COURS_ORIGINE".equals(demande.getStatut())) {
            throw new BusinessException("La demande doit avoir été validée par l'université d'origine", "INVALID_STATUT");
        }
        Equivalence equivalence = EquivalenceMapper.toEntity(request, demande);
        equivalence = equivalenceRepository.save(equivalence);
        for (CoursEquivalentRequestDTO coursReq : request.getCoursEquivalents()) {
            CoursEquivalent cours = CoursEquivalentMapper.toEntity(coursReq, equivalence);
            coursEquivalentRepository.save(cours);
        }
        demande.setStatut("EN_COURS_EQUIVALENCE");
        demandeRepository.save(demande);
        ajouterAudit(demandeId, "ANALYSE_EQUIVALENCE", "Analyse d'équivalence réalisée", responsableId);
    }

    @Override
    public void validerParUniversiteAccueil(UUID demandeId, boolean accepte, String commentaire, UUID responsableId) throws ResourceNotFoundException, BusinessException {
        DemandeTransfert demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId));
        if (!"EN_COURS_EQUIVALENCE".equals(demande.getStatut())) {
            throw new BusinessException("La demande doit être en cours d'analyse des équivalences", "INVALID_STATUT");
        }
        if (accepte) {
            demande.setStatut("ACCEPTEE");
            demande.setCommentaireRefus(null);
        } else {
            demande.setStatut("REFUSEE");
            demande.setCommentaireRefus(commentaire);
        }
        demandeRepository.save(demande);
        ajouterAudit(demandeId, "AVIS_ACCUEIL", "Université d'accueil : " + (accepte ? "accepté" : "refusé") + (commentaire != null ? " - " + commentaire : ""), responsableId);
    }

    @Override
    public void definirCoursReprise(UUID demandeId, List<CoursEquivalentRequestDTO> coursAReprendre, UUID responsableId) throws ResourceNotFoundException, BusinessException {
        DemandeTransfert demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId));
        if (!"ACCEPTEE".equals(demande.getStatut())) {
            throw new BusinessException("Seule une demande acceptée peut avoir une liste de cours à reprendre", "INVALID_STATUT");
        }
        Equivalence equivalence = demande.getEquivalence();
        if (equivalence == null) {
            equivalence = new Equivalence();
            equivalence.setDemande(demande);
            equivalence = equivalenceRepository.save(equivalence);
        }
        coursEquivalentRepository.deleteAll(equivalence.getCoursEquivalents());
        equivalence.getCoursEquivalents().clear();
        for (CoursEquivalentRequestDTO coursReq : coursAReprendre) {
            CoursEquivalent cours = CoursEquivalentMapper.toEntity(coursReq, equivalence);
            coursEquivalentRepository.save(cours);
            equivalence.getCoursEquivalents().add(cours);
        }
        equivalenceRepository.save(equivalence);
        ajouterAudit(demandeId, "DEFINITION_COURS", "Cours à reprendre définis", responsableId);
    }

    // ========== Consultation ==========
    @Override
    @Transactional(readOnly = true)
    public DemandeTransfertResponseDTO suivreDemande(UUID demandeId) throws ResourceNotFoundException {
        DemandeTransfert demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId));
        return DemandeTransfertMapper.toDTO(demande);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandeTransfertResponseDTO> historiqueTransferts(UUID etudiantId) throws ResourceNotFoundException {
        if (!etudiantRepository.existsById(etudiantId)) {
            throw new ResourceNotFoundException("Étudiant", etudiantId);
        }
        return demandeRepository.findByEtudiantId(etudiantId).stream()
                .map(DemandeTransfertMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ========== Administration centrale ==========
    @Override
    @Transactional(readOnly = true)
    public void detecterDoublons(String numeroEtudiant) throws BusinessException {
        Etudiant etudiant = etudiantRepository.findByNumeroEtudiant(numeroEtudiant)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", numeroEtudiant));
        List<DemandeTransfert> demandes = demandeRepository.findByEtudiantId(etudiant.getId());
        long count = demandes.stream().filter(d -> !List.of("REFUSEE", "ANNULEE").contains(d.getStatut())).count();
        if (count > 1) {
            throw new BusinessException("Plusieurs demandes actives détectées pour cet étudiant", "FRAUD_DETECTED");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DemandeTransfertResponseDTO> auditerTransferts(Pageable pageable) {
        return demandeRepository.findAll(pageable).map(DemandeTransfertMapper::toDTO);
    }

    // ========== Méthodes privées ==========
    private void ajouterAudit(UUID demandeId, String action, String details, UUID utilisateurId) {
        TransfertAudit audit = TransfertAudit.builder()
                .action(action)
                .details(details)
                .dateAction(LocalDateTime.now())
                .utilisateurId(utilisateurId)
                .demande(DemandeTransfert.builder().id(demandeId).build())
                .build();
        auditRepository.save(audit);
    }
}