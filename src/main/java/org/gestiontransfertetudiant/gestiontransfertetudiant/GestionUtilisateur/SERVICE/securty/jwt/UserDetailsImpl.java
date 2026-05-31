package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private final UUID id;
    private final String login;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean actif;

    public static UserDetailsImpl build(Utilisateur utilisateur) {
        Set<GrantedAuthority> authorities = utilisateur.getUtilisateurRoles().stream()
                .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRole().getNom()))
                .collect(Collectors.toSet());
        return new UserDetailsImpl(
                utilisateur.getUtilisateurId(),
                utilisateur.getLogin(),
                utilisateur.getMotDePasseHash(),
                authorities,
                utilisateur.getActif()
        );
    }



    @Override public String getUsername() { return login; }
    @Override public String getPassword() { return password; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return actif; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return actif; }
}