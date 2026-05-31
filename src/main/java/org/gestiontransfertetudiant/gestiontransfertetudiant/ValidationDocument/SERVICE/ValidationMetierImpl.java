package org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.SERVICE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.repository.ValidationRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.BusinessException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.ResourceNotFoundException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.mapper.EtapeValidationMapper;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.mapper.HistoriqueValidationMapper;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.mapper.ValidationMapper;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.mapper.WorkflowMapper;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.request.ValidationRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.request.WorkflowRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.response.ValidationResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.response.WorkflowResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.entity.EtapeValidation;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.entity.HistoriqueValidation;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.entity.Validation;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.entity.Workflow;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.repository.*;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.SERVICE.IValidationMetier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ValidationMetierImpl implements IValidationMetier {

    private final ValidationWorkflowRepository  validationRepository;
    private final EtapeValidationRepository etapeRepository;
    private final WorkFlowRepository workFlowRepository;
    private final HistoriqueValidationRepository historiqueRepository;

    @Override
    @Transactional(readOnly = true)
    public long getValidationsEnAttente() {
        return validationRepository.countByStatut("EN_ATTENTE");
    }

    @Override
    public ValidationResponseDTO soumettreValidation(ValidationRequestDTO request, UUID soumissionneurId) throws BusinessException {
        // Vérifier qu'il n'existe pas déjà une validation en cours pour cette entité
        List<Validation> existantes = validationRepository.findByEntiteIdAndEntiteType(request.getEntiteId(), request.getEntiteType());
        if (existantes.stream().anyMatch(v -> !List.of("VALIDEE", "REJETEE", "ANNULEE").contains(v.getStatut()))) {
            throw new BusinessException("Une validation est déjà en cours pour cette entité", "VALIDATION_EN_COURS");
        }
        Validation workflow = validationRepository.findById(request.getWorkflowId())
                .orElseThrow();
        Validation validation = ValidationMapper.toEntity(request, workflow);
        validation = validationRepository.save(validation);
        // Ajout historique
        ajouterHistorique(validation, "SOUMISSION", "Soumission par l'utilisateur " + soumissionneurId, soumissionneurId);
        return ValidationMapper.toDTO(validation);
    }

    @Override
    public ValidationResponseDTO valider(UUID validationId, UUID valideurId, String commentaire) throws BusinessException {
        Validation validation = validationRepository.findById(validationId)
                .orElseThrow(() -> new ResourceNotFoundException("Validation", validationId));
        if (!"EN_ATTENTE".equals(validation.getStatut())) {
            throw new BusinessException("Seule une validation en attente peut être validée", "STATUT_INVALIDE");
        }
        // Vérifier que le valideur a le rôle requis (à faire via un service d'autorisation)
        // Ici on simule la vérification
        validation.setStatut("VALIDEE");
        validation.setDateValidation(LocalDateTime.now());
        validation.setCommentaire(commentaire);
        validation.setValideurId(valideurId);
        validationRepository.save(validation);
        ajouterHistorique(validation, "VALIDATION", "Validée par " + valideurId + " : " + commentaire, valideurId);
        return ValidationMapper.toDTO(validation);
    }

    @Override
    public ValidationResponseDTO rejeter(UUID validationId, UUID valideurId, String motif) throws BusinessException {
        Validation validation = validationRepository.findById(validationId)
                .orElseThrow(() -> new ResourceNotFoundException("Validation", validationId));
        if (!"EN_ATTENTE".equals(validation.getStatut())) {
            throw new BusinessException("Seule une validation en attente peut être rejetée", "STATUT_INVALIDE");
        }
        validation.setStatut("REJETEE");
        validation.setDateValidation(LocalDateTime.now());
        validation.setCommentaire(motif);
        validation.setValideurId(valideurId);
        validationRepository.save(validation);
        ajouterHistorique(validation, "REJET", "Rejetée par " + valideurId + " : " + motif, valideurId);
        return ValidationMapper.toDTO(validation);
    }

    @Override
    public ValidationResponseDTO demanderRevision(UUID validationId, UUID demandeurId, String commentaire) throws BusinessException {
        Validation validation = validationRepository.findById(validationId)
                .orElseThrow(() -> new ResourceNotFoundException("Validation", validationId));
        if (!"EN_ATTENTE".equals(validation.getStatut())) {
            throw new BusinessException("Seule une validation en attente peut être révisée", "STATUT_INVALIDE");
        }
        validation.setStatut("REVISION");
        validation.setCommentaire(commentaire);
        validationRepository.save(validation);
        ajouterHistorique(validation, "DEMANDE_REVISION", "Révision demandée par " + demandeurId + " : " + commentaire, demandeurId);
        return ValidationMapper.toDTO(validation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidationResponseDTO> getHistoriqueValidations(UUID entiteId, String entiteType) {
        return validationRepository.findByEntiteIdAndEntiteType(entiteId, entiteType).stream()
                .map(ValidationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void annulerValidation(UUID validationId, UUID annuleurId) throws BusinessException {
        Validation validation = validationRepository.findById(validationId)
                .orElseThrow(() -> new ResourceNotFoundException("Validation", validationId));
        if ("VALIDEE".equals(validation.getStatut())) {
            throw new BusinessException("Une validation déjà validée ne peut pas être annulée", "STATUT_INVALIDE");
        }
        validation.setStatut("ANNULEE");
        validationRepository.save(validation);
        ajouterHistorique(validation, "ANNULATION", "Annulée par l'administrateur " + annuleurId, annuleurId);
    }

    @Override
    public void attribuerValidateur(UUID validationId, UUID valideurId, UUID adminId) throws BusinessException {
        Validation validation = validationRepository.findById(validationId)
                .orElseThrow(() -> new ResourceNotFoundException("Validation", validationId));
        validation.setValideurId(valideurId);
        validationRepository.save(validation);
        ajouterHistorique(validation, "ATTRIBUTION_VALIDEUR", "Validateur " + valideurId + " attribué par admin " + adminId, adminId);
    }

    @Override
    public WorkflowResponseDTO definirWorkflow(WorkflowRequestDTO request, UUID adminId) throws BusinessException {
        if (validationRepository.findByNom(request.getNom()).isPresent()) {
            throw new BusinessException("Un workflow avec ce nom existe déjà", "DUPLICATE_WORKFLOW");
        }
        Workflow workflow = WorkflowMapper.toEntity(request);
        workflow =workFlowRepository.save(workflow);
        // Sauvegarder les étapes
        for (var etapeReq : request.getEtapes()) {
            EtapeValidation etape = EtapeValidationMapper.toEntity(etapeReq, workflow);
            etapeRepository.save(etape);
            workflow.getEtapes().add(etape);
        }
        ajouterHistoriqueWorkflow(workflow, "CREATION", "Workflow créé par admin " + adminId, adminId);
        return WorkflowMapper.toDTO(workflow);
    }

    @Override
    public void suspendreValidation(UUID validationId, UUID adminId, String raison) throws BusinessException {
        Validation validation = validationRepository.findById(validationId)
                .orElseThrow(() -> new ResourceNotFoundException("Validation", validationId));
        if ("VALIDEE".equals(validation.getStatut()) || "REJETEE".equals(validation.getStatut())) {
            throw new BusinessException("Une validation terminée ne peut pas être suspendue", "STATUT_INVALIDE");
        }
        validation.setStatut("SUSPENDUE");
        validationRepository.save(validation);
        ajouterHistorique(validation, "SUSPENSION", "Suspendue par admin " + adminId + " : " + raison, adminId);
    }

    @Override
    public void reprendreValidation(UUID validationId, UUID adminId) throws BusinessException {
        Validation validation = validationRepository.findById(validationId)
                .orElseThrow(() -> new ResourceNotFoundException("Validation", validationId));
        if (!"SUSPENDUE".equals(validation.getStatut())) {
            throw new BusinessException("Seule une validation suspendue peut être reprise", "STATUT_INVALIDE");
        }
        validation.setStatut("EN_ATTENTE");
        validationRepository.save(validation);
        ajouterHistorique(validation, "REPRISE", "Reprise par admin " + adminId, adminId);
    }

    // Méthodes utilitaires privées
    private void ajouterHistorique(Validation validation, String action, String details, UUID utilisateurId) {
        HistoriqueValidation historique = HistoriqueValidationMapper.toEntity(validation, action, details, utilisateurId);
        historiqueRepository.save(historique);
    }

    private void ajouterHistoriqueWorkflow(Workflow workflow, String action, String details, UUID utilisateurId) {
        // Si on souhaite tracer l'historique des workflows, on peut utiliser une entité similaire
        log.info("Workflow {} - {} : {} (par {})", workflow.getId(), action, details, utilisateurId);
    }
}