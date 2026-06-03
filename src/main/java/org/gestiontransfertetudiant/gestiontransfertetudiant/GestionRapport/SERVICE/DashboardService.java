package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionRapport.SERVICE;

import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.repository.DemandeTransfertRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.repository.EtablissementAnterieurRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository.UtilisateurRepository;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionFraude.SERVICE.IFraudeMetier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UtilisateurRepository utilisateurRepository;
    private final DemandeTransfertRepository demandeRepository;
    private final EtablissementAnterieurRepository etabRepo;
    private final ObjectProvider<IFraudeMetier> fraudeMetierProvider;

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        long users = utilisateurRepository.count();
        long demandes = demandeRepository.count();
        double tauxFraude = 0.0;
        try {
            IFraudeMetier fraude = fraudeMetierProvider.getIfAvailable();
            if (fraude != null) {
                tauxFraude = fraude.getTauxFraude();
            }
        } catch (Exception ignored) {}

        stats.put("statsUtilisateurs", users);
        stats.put("statsTransferts", demandes);
        stats.put("tauxFraude", tauxFraude);
        return stats;
    }

    public Map<String, Object> getTransfersLastMonths(int months) {
        YearMonth now = YearMonth.now();
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        for (int i = months - 1; i >= 0; i--) {
            YearMonth m = now.minusMonths(i);
            LocalDate start = m.atDay(1);
            LocalDate end = m.atEndOfMonth();
            long count = demandeRepository.findByDateDemandeBetween(start, end).size();
            labels.add(m.getMonth().toString().substring(0,3));
            values.add(count);
        }
        Map<String, Object> res = new HashMap<>();
        res.put("labels", labels);
        res.put("values", values);
        return res;
    }

    public Map<String, Object> getEtablissementDistribution() {
        List<String> labels = Arrays.asList("Publique", "Privé", "Étranger");
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("Publique", 0L);
        counts.put("Privé", 0L);
        counts.put("Étranger", 0L);

        List<?> all = etabRepo.findAll();
        for (Object o : all) {
            try {
                // reflectively access pays and nom
                String pays = (String) o.getClass().getMethod("getPays").invoke(o);
                String nom = (String) o.getClass().getMethod("getNom").invoke(o);
                if (pays != null && !"France".equalsIgnoreCase(pays)) {
                    counts.computeIfPresent("Étranger", (k,v) -> v+1);
                } else if (nom != null && nom.toLowerCase().contains("universit")) {
                    counts.computeIfPresent("Publique", (k,v) -> v+1);
                } else {
                    counts.computeIfPresent("Privé", (k,v) -> v+1);
                }
            } catch (Exception e) {
                // ignore
            }
        }
        Map<String, Object> res = new HashMap<>();
        res.put("labels", new ArrayList<>(counts.keySet()));
        res.put("values", new ArrayList<>(counts.values()));
        return res;
    }
}
