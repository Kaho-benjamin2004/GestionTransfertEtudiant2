# Frontend Angular - Gestion des Transferts d'Étudiants

Application frontend Angular pour le système de gestion des transferts d'étudiants.

## Architecture

### Structure du projet

```
src/
├── app/
│   ├── core/                    # Services et utilities principales
│   │   ├── guards/              # Guards de protection des routes
│   │   ├── interceptors/        # Interceptors HTTP
│   │   ├── models/              # Interfaces et modèles de données
│   │   └── services/            # Services (Auth, etc.)
│   ├── features/                # Modules feature
│   │   ├── auth/                # Authentification (login, register)
│   │   ├── dashboard/           # Tableau de bord
│   │   ├── etudiant/            # Fonctionnalités étudiants
│   │   └── admin/               # Fonctionnalités admin
│   ├── shared/                  # Composants partagés
│   └── app.routes.ts            # Routes principales
```

## Fonctionnalités

### Authentification
- **Login**: Connexion des utilisateurs
- **Register**: Inscription de nouveaux utilisateurs
- **JWT Tokens**: Gestion des tokens d'accès et refresh
- **Password Change**: Changement du mot de passe

### Sécurité
- **AuthGuard**: Protège les routes non-authentifiées
- **NoAuthGuard**: Protège les routes d'authentification (login, register)
- **AuthInterceptor**: Ajoute automatiquement les tokens JWT aux requêtes
- **Role-based Access**: Contrôle d'accès basé sur les rôles

### Dashboard
- Vue d'ensemble du système
- Navigation par rôle
- Déconnexion

### Profil
- Affichage des informations utilisateur
- Changement de mot de passe

## Installation

### Prérequis
- Node.js 18+
- npm 9+

### Étapes

```bash
# Installer les dépendances
npm install

# Démarrer le serveur de développement
npm start

# Build pour la production
npm run build

# Tests
npm test
```

## Routes

| Route | Composant | Protégé | Rôles |
|-------|-----------|---------|-------|
| `/login` | LoginComponent | ✗ | - |
| `/register` | RegisterComponent | ✗ | - |
| `/dashboard` | DashboardComponent | ✓ | Tous |
| `/profile` | ProfileComponent | ✓ | Tous |
| `/etudiant` | EtudiantComponent | ✓ | ETUDIANT |
| `/admin` | AdminComponent | ✓ | ADMIN |
| `/unauthorized` | UnauthorizedComponent | ✗ | - |

## Services

### AuthService
- `login(credentials: LoginRequest)`: Connexion utilisateur
- `register(userData: RegisterRequest)`: Inscription
- `refreshToken(token: string)`: Rafraîchir le token
- `changePassword(request: ChangePasswordRequest)`: Changer le mot de passe
- `logout(refreshToken: string)`: Déconnexion
- `isAuthenticated()`: Vérifier l'authentification
- `hasRole(role: string)`: Vérifier un rôle
- `hasAnyRole(roles: string[])`: Vérifier plusieurs rôles

## Configuration de l'API

L'API backend est accessible à: `http://localhost:8080`

Les endpoints d'authentification:
- `POST /auth/login`
- `POST /auth/register`
- `POST /auth/refresh`
- `POST /auth/change-password`
- `POST /auth/logout`

## Développement futur

- [ ] Module Étudiant complète
- [ ] Module Gestion des Dossiers
- [ ] Module Admin complète
- [ ] Module Notifications
- [ ] Tests unitaires et E2E
- [ ] Optimisation de la performance
- [ ] Amélioration de l'UX/UI

## Styles

Le projet utilise SCSS pour les styles avec un système de variables personnalisées.
Tous les composants sont standalone et utilisent les styles locaux.

## Navigation

Après l'authentification, les utilisateurs sont redirigés vers le dashboard.
La barre latérale affiche les options de navigation selon le rôle de l'utilisateur.
