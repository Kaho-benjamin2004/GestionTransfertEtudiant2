package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.SERVICE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.StatistiquesDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.reponse.*;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.request.CreditRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.request.EtablissementAnterieurRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.request.NoteRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.request.SanctionRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.entity.*;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.mapper.*;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.repository.*;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.ProfilRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Profil;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Utilisateur;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.ProfilRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.UtilisateurRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.BusinessException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EtudiantMetierImpl implements IEtudiantMetier {

    private final EtudiantRepository etudiantRepository;
    private final ParcoursAcademiqueRepository parcoursRepository;
    private final UniteEnseignementRepository ueRepository;
    private final NoteRepository noteRepository;
    private  final UtilisateurRepository utilisateurRepository;
    private final CreditRepository creditRepository;
    private final SanctionRepository sanctionRepository;
    private final EtablissementAnterieurRepository etabAnterieurRepository;
    private final ProfilRepository profilRepository;
    private final CalculMoyenneStrategy calculMoyenneStrategy;

    // ========== Cas d'utilisation Étudiant ==========


    @Override
    public EtudiantResponseDTO creerEtudiantPourUtilisateur(UUID utilisateurId, String numeroEtudiant) throws BusinessException {
        Utilisateur utilisateur = utilisateurRepository.findByUtilisateurId(utilisateurId);
//                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", utilisateurId));
        if (etudiantRepository.findByUtilisateurId(utilisateurId).isPresent()) {
            throw new BusinessException("Un étudiant est déjà associé à cet utilisateur", "DUPLICATE");
        }
        Etudiant etudiant = new Etudiant();
        etudiant.setUtilisateur(utilisateur);
        etudiant.setNumeroEtudiant(numeroEtudiant != null ? numeroEtudiant : "ETU" + System.currentTimeMillis());
        etudiant.setDateInscription(LocalDate.now());
        etudiant = etudiantRepository.save(etudiant);
        return EtudiantMapper.toDTO(etudiant);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalCreditsAcquis(UUID etudiantId) throws ResourceNotFoundException {
        if (!etudiantRepository.existsById(etudiantId)) {
            throw new ResourceNotFoundException("Étudiant", etudiantId);
        }
        List<Credit> credits = creditRepository.findByEtudiantId(etudiantId);
        return credits.stream()
                .filter(c -> "Acquis".equals(c.getStatut()))
                .mapToInt(Credit::getNombre)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public EtudiantResponseDTO consulterHistorique(UUID etudiantId) throws ResourceNotFoundException {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", etudiantId));
        return EtudiantMapper.toDTO(etudiant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditResponseDTO> consulterCreditsValides(UUID etudiantId) throws ResourceNotFoundException {
        if (!etudiantRepository.existsById(etudiantId)) {
            throw new ResourceNotFoundException("Étudiant", etudiantId);
        }
        return creditRepository.findByEtudiantId(etudiantId).stream()
                .filter(c -> "Acquis".equals(c.getStatut()))
                .map(CreditMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanctionResponseDTO> consulterSanctions(UUID etudiantId) throws ResourceNotFoundException {
        if (!etudiantRepository.existsById(etudiantId)) {
            throw new ResourceNotFoundException("Étudiant", etudiantId);
        }
        return sanctionRepository.findByEtudiantId(etudiantId).stream()
                .map(SanctionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EtablissementAnterieurResponseDTO ajouterEtablissementAnterieur(UUID etudiantId, EtablissementAnterieurRequestDTO request) throws BusinessException {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", etudiantId));
        EtablissementAnterieur etab = EtablissementAnterieurMapper.toEntity(request, etudiant);
        etab = etabAnterieurRepository.save(etab);
        return EtablissementAnterieurMapper.toDTO(etab);
    }

    @Override
    public void modifierInformationsPersonnelles(UUID utilisateurId, ProfilRequestDTO profilRequest) throws ResourceNotFoundException {
        Profil profil = profilRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil de l'utilisateur", utilisateurId));
        profil.setNom(profilRequest.getNom());
        profil.setPrenom(profilRequest.getPrenom());
        profil.setEmail(profilRequest.getEmail());
        profil.setTelephone(profilRequest.getTelephone());
        profil.setMatriculeNational(profilRequest.getMatriculeNational());
        profilRepository.save(profil);
    }

    // ========== Cas d'utilisation Agent académique ==========

    @Override
    @Transactional(readOnly = true)
    public Page<EtudiantResponseDTO> rechercherEtudiants(String critere, Pageable pageable) {
        if (critere == null || critere.trim().isEmpty()) {
            return etudiantRepository.findAll(pageable).map(EtudiantMapper::toDTO);
        }
        return etudiantRepository.rechercherParCritere(critere.trim(), pageable)
                .map(EtudiantMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public EtudiantResponseDTO consulterDossierComplet(UUID etudiantId) throws ResourceNotFoundException {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", etudiantId));
        return EtudiantMapper.toDTO(etudiant);
    }

    @Override
    public CreditResponseDTO ajouterCredits(CreditRequestDTO request) throws ResourceNotFoundException, BusinessException {
        ParcoursAcademique parcours = parcoursRepository.findById(request.getParcoursAcademiqueId())
                .orElseThrow(() -> new ResourceNotFoundException("Parcours académique", request.getParcoursAcademiqueId()));
        Credit credit = CreditMapper.toEntity(request, parcours);
        credit = creditRepository.save(credit);
        return CreditMapper.toDTO(credit);
    }

    @Override
    public SanctionResponseDTO ajouterSanction(SanctionRequestDTO request) throws ResourceNotFoundException, BusinessException {
        Etudiant etudiant = etudiantRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", request.getEtudiantId()));
        Sanction sanction = SanctionMapper.toEntity(request, etudiant);
        sanction = sanctionRepository.save(sanction);
        return SanctionMapper.toDTO(sanction);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] genererReleveNotes(UUID etudiantId, UUID parcoursId) throws ResourceNotFoundException {
        // Implémentation à compléter avec PdfGenerationService
        log.warn("Génération PDF non encore implémentée");
        return new byte[0];
    }

    @Override
    public void validerParcours(UUID parcoursId) throws ResourceNotFoundException, BusinessException {
        ParcoursAcademique parcours = parcoursRepository.findById(parcoursId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcours académique", parcoursId));
        if (!"En cours".equals(parcours.getStatut())) {
            throw new BusinessException("Seul un parcours en cours peut être validé", "INVALID_STATE");
        }
        BigDecimal moyenne = calculerMoyenneGenerale(parcours.getEtudiant().getId());
        parcours.setMoyenne(moyenne);
        parcours.setStatut("Validé");
        parcoursRepository.save(parcours);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculerMoyenneGenerale(UUID etudiantId) throws ResourceNotFoundException {
        return calculMoyenneStrategy.calculer(etudiantId);
    }

    // ========== Cas d'utilisation Administration centrale ==========

    @Override
    @Transactional(readOnly = true)
    public StatistiquesDTO exporterStatistiques(String filiere, String anneeUniversitaire) throws BusinessException {
        if (filiere == null || anneeUniversitaire == null) {
            throw new BusinessException("La filière et l'année universitaire sont obligatoires", "MISSING_PARAMS");
        }
        // Données simulées - à remplacer par des calculs réels
        return StatistiquesDTO.builder()
                .filiere(filiere)
                .anneeUniversitaire(anneeUniversitaire)
                .nombreEtudiants(42)
                .moyenneGenerale(new BigDecimal("12.5"))
                .build();
    }

    // ========== Cas d'utilisation Commission pédagogique ==========

    @Override
    @Transactional(readOnly = true)
    public ComparaisonProgrammeDTO comparerProgrammes(UUID ueId1, UUID ueId2) throws ResourceNotFoundException {
        UniteEnseignement ue1 = ueRepository.findById(ueId1)
                .orElseThrow(() -> new ResourceNotFoundException("UE", ueId1));
        UniteEnseignement ue2 = ueRepository.findById(ueId2)
                .orElseThrow(() -> new ResourceNotFoundException("UE", ueId2));
        List<String> similitudes = ue1.getCredits().equals(ue2.getCredits()) ?
                List.of("Même nombre de crédits") : List.of();
        List<String> differences = !ue1.getCode().equals(ue2.getCode()) ?
                List.of("Code différent") : List.of();
        return ComparaisonProgrammeDTO.builder()
                .unite1(UniteEnseignementMapper.toDTO(ue1))
                .unite2(UniteEnseignementMapper.toDTO(ue2))
                .similitudes(similitudes)
                .differences(differences)
                .equivalencesCredits(ue1.getCredits().equals(ue2.getCredits()) ? ue1.getCredits() : 0)
                .build();
    }

    // ========== Méthodes supplémentaires ==========

    @Override
    public NoteResponseDTO ajouterNote(NoteRequestDTO request) throws ResourceNotFoundException, BusinessException {
        ParcoursAcademique parcours = parcoursRepository.findById(request.getParcoursAcademiqueId())
                .orElseThrow(() -> new ResourceNotFoundException("Parcours académique", request.getParcoursAcademiqueId()));
        UniteEnseignement ue = ueRepository.findById(request.getUniteEnseignementId())
                .orElseThrow(() -> new ResourceNotFoundException("Unité d'enseignement", request.getUniteEnseignementId()));
        Note note = NoteMapper.toEntity(request, parcours, ue);
        note = noteRepository.save(note);
        return NoteMapper.toDTO(note);
    }

    @Override
    public UniteEnseignementResponseDTO sauvegarderUE(org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DTO.request.UniteEnseignementRequestDTO request) throws BusinessException {
        if (ueRepository.findByCode(request.getCode()).isPresent()) {
            throw new BusinessException("Une UE avec le code " + request.getCode() + " existe déjà", "DUPLICATE_CODE");
        }
        UniteEnseignement ue = UniteEnseignementMapper.toEntity(request);
        ue = ueRepository.save(ue);
        return UniteEnseignementMapper.toDTO(ue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UniteEnseignementResponseDTO> listerToutesUE() {
        return ueRepository.findAll().stream()
                .map(UniteEnseignementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UniteEnseignementResponseDTO getUEById(UUID id) throws ResourceNotFoundException {
        UniteEnseignement ue = ueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UE", id));
        return UniteEnseignementMapper.toDTO(ue);
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getEtudiantIdByUtilisateurId(UUID utilisateurId) throws ResourceNotFoundException {
        Etudiant etudiant = etudiantRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun étudiant associé à cet utilisateur", utilisateurId));
        return etudiant.getId();
    }
}