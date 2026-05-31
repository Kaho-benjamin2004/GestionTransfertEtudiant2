package org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "validation")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Validation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID entiteId;
    private String entiteType;
    private Integer etape;
    private String nom;
    private String statut; // EN_ATTENTE, VALIDEE, REJETEE, SUSPENDUE
    private LocalDateTime dateSoumission;
    private LocalDateTime dateValidation;
    private String commentaire;
    private UUID valideurId;
    @OneToMany(mappedBy = "validation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoriqueValidation> historiques = new ArrayList<>();
}