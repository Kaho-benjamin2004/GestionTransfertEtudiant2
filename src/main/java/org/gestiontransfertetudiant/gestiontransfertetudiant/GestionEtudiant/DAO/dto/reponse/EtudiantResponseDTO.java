package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.reponse;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class EtudiantResponseDTO {
    private UUID id;
    private String numeroEtudiant;
    private LocalDate dateInscription;
    private String parcoursActuel;
    private String niveau;
    private UUID utilisateurId;
    private String nom;    // dénormalisé depuis le profil
    private String prenom;
    private String Telephone;
    private String MatriculeNational;
    private String email;
    private List<ParcoursAcademiqueResponseDTO> parcoursAcademiques;
    private List<SanctionResponseDTO> sanctions;
    private List<EtablissementAnterieurResponseDTO> etablissementsAnterieurs;

}